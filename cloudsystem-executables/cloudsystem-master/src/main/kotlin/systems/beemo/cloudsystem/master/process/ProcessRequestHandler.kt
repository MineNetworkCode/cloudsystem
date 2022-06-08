package systems.beemo.cloudsystem.master.process

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType
import systems.beemo.cloudsystem.master.groups.bungee.BungeeGroupHandler
import systems.beemo.cloudsystem.master.groups.spigot.SpigotGroupHandler
import systems.beemo.cloudsystem.master.network.protocol.outgoing.process.PacketOutRequestProcess
import systems.beemo.cloudsystem.master.network.utils.NetworkUtils
import systems.beemo.cloudsystem.master.process.models.CloudProcess
import systems.beemo.cloudsystem.master.worker.WorkerRegistry
import java.util.*

class ProcessRequestHandler(
    private val networkUtils: NetworkUtils,
    private val workerRegistry: WorkerRegistry,
    private val bungeeGroupHandler: BungeeGroupHandler,
    private val spigotGroupHandler: SpigotGroupHandler,
    private val processRegistry: ProcessRegistry
) {

    private val logger: Logger = LoggerFactory.getLogger(ProcessRequestHandler::class.java)

    fun requestProcessesOnConnect(workerInfo: WorkerInfo) {
        for (responsibleGroup in workerInfo.responsibleGroups) {
            val bungeeGroup = bungeeGroupHandler.getGroup(responsibleGroup)
            val spigotGroup = spigotGroupHandler.getGroup(responsibleGroup)

            when {
                bungeeGroup != null -> {
                    val currentlyRunning = processRegistry.getRunningProcessCount(bungeeGroup.name)

                    if (bungeeGroup.minServersOnline != 1) {
                        if (currentlyRunning >= bungeeGroup.minServersOnline) continue

                        val requestCount = bungeeGroup.minServersOnline - currentlyRunning
                        val memoryUsageAfterRequest = workerInfo.currentMemoryConsumption + (bungeeGroup.maxMemory * requestCount)

                        if (memoryUsageAfterRequest > workerInfo.memory) {
                            logger.error("Could not handle the request. It would consume too much memory!")
                            continue
                        }

                        this.requestMultipleProcesses(
                            groupName = bungeeGroup.name,
                            type = ProcessType.BUNGEE,
                            minMemory = bungeeGroup.minMemory,
                            maxMemory = bungeeGroup.maxMemory,
                            maxPlayers = bungeeGroup.maxPlayers,
                            joinPower = bungeeGroup.joinPower,
                            lobbyServer = false,
                            dynamicServer = false,
                            staticServer = false,
                            maintenance = bungeeGroup.maintenance,
                            requestCount = requestCount
                        )
                    } else {
                        if (currentlyRunning >= bungeeGroup.minServersOnline) {
                            continue
                        }

                        this.requestProcess(
                            groupName = bungeeGroup.name,
                            type = ProcessType.BUNGEE,
                            minMemory = bungeeGroup.minMemory,
                            maxMemory = bungeeGroup.maxMemory,
                            maxPlayers = bungeeGroup.maxPlayers,
                            joinPower = bungeeGroup.joinPower,
                            lobbyServer = false,
                            dynamicServer = false,
                            staticServer = false,
                            maintenance = bungeeGroup.maintenance,
                        )
                    }
                }
                spigotGroup != null -> {
                    val currentlyRunning = processRegistry.getRunningProcessCount(spigotGroup.name)

                    if (spigotGroup.minServersOnline != 1) {
                        val requestCount = spigotGroup.minServersOnline - currentlyRunning

                        val memoryUsageAfterRequest = workerInfo.currentMemoryConsumption + (spigotGroup.maxMemory * requestCount)

                        if (memoryUsageAfterRequest > workerInfo.memory) {
                            logger.error("Could not handle the request. It would consume too much memory!")
                            continue
                        }

                        this.requestMultipleProcesses(
                            groupName = spigotGroup.name,
                            type = ProcessType.SPIGOT,
                            minMemory = spigotGroup.minMemory,
                            maxMemory = spigotGroup.maxMemory,
                            maxPlayers = spigotGroup.maxPlayers,
                            joinPower = spigotGroup.joinPower,
                            lobbyServer = spigotGroup.lobbyServer,
                            dynamicServer = spigotGroup.dynamicServer,
                            staticServer = spigotGroup.staticServer,
                            maintenance = spigotGroup.maintenance,
                            requestCount = requestCount
                        )
                    } else {
                        if (currentlyRunning >= spigotGroup.minServersOnline) {
                            continue
                        }

                        this.requestProcess(
                            groupName = spigotGroup.name,
                            type = ProcessType.SPIGOT,
                            minMemory = spigotGroup.minMemory,
                            maxMemory = spigotGroup.maxMemory,
                            maxPlayers = spigotGroup.maxPlayers,
                            joinPower = spigotGroup.joinPower,
                            lobbyServer = spigotGroup.lobbyServer,
                            dynamicServer = spigotGroup.dynamicServer,
                            staticServer = spigotGroup.staticServer,
                            maintenance = spigotGroup.maintenance,
                        )
                    }
                }
                else -> {
                    logger.error("Somehow no group was found matching the pattern. This should not happen!")
                }
            }
        }
    }

    fun requestProcess(
        groupName: String,
        type: ProcessType,
        minMemory: Int,
        maxMemory: Int,
        maxPlayers: Int,
        joinPower: Int,
        lobbyServer: Boolean,
        dynamicServer: Boolean,
        staticServer: Boolean,
        maintenance: Boolean
    ) {
        val runningProcessCount = processRegistry.getRunningProcessCount(groupName) + 1
        val name = "$groupName-${if (runningProcessCount >= 10) "$runningProcessCount" else "0$runningProcessCount"}"

        val workerInfo = workerRegistry.getLeastUsedWorker(groupName)

        if (workerInfo == null) {
            logger.error("Could not find any usable worker for CloudProcess:(Name=${name})")
            return
        }

        val workerChannel = workerInfo.channel

        if (workerChannel == null) {
            logger.error("Somehow the workers channel is null... This should not happen!")
            return
        }

        val ip = workerChannel.remoteAddress().toString().replace("/", "").split(":")[0]

        val cloudProcess: CloudProcess

        if (type == ProcessType.BUNGEE) {
            cloudProcess = CloudProcess(
                groupName = groupName,
                name = name,
                uuid = UUID.randomUUID().toString(),
                ip = ip,
                type = type,
                stage = ProcessStage.STARTING,
                maxMemory = minMemory,
                minMemory = maxMemory,
                port = this.findFreePort(25565),
                maxPlayers = maxPlayers,
                joinPower = joinPower,
                maintenance = maintenance
            )
        } else {
            val freePort = if (lobbyServer) this.findFreePort(25000) else this.findFreePort(35000)
            cloudProcess = CloudProcess(
                groupName = groupName,
                name = name,
                uuid = UUID.randomUUID().toString(),
                ip = ip,
                type = type,
                stage = ProcessStage.STARTING,
                maxMemory = minMemory,
                minMemory = maxMemory,
                port = freePort,
                maxPlayers = maxPlayers,
                joinPower = joinPower,
                maintenance = maintenance,
                lobbyServer = lobbyServer,
                dynamicServer = dynamicServer,
                staticServer = staticServer
            )
        }

        networkUtils.sendPacketAsync(PacketOutRequestProcess(cloudProcess), workerChannel)

        processRegistry.registerProcess(cloudProcess)
        logger.info("Requested new CloudProcess:(Name=${cloudProcess.name})")
    }

    fun requestMultipleProcesses(
        groupName: String,
        type: ProcessType,
        minMemory: Int,
        maxMemory: Int,
        maxPlayers: Int,
        joinPower: Int,
        lobbyServer: Boolean,
        dynamicServer: Boolean,
        staticServer: Boolean,
        maintenance: Boolean,
        requestCount: Int
    ) {
        (1..requestCount).forEach { _ ->
            this.requestProcess(
                groupName = groupName,
                type = type,
                minMemory = minMemory,
                maxMemory = maxMemory,
                maxPlayers = maxPlayers,
                joinPower = joinPower,
                lobbyServer = lobbyServer,
                dynamicServer = dynamicServer,
                staticServer = staticServer,
                maintenance = maintenance
            )
        }
    }

    private fun findFreePort(startPort: Int): Int {
        var startPortCopy = startPort

        while (processRegistry.isPortInUse(startPortCopy)) {
            startPortCopy++
        }

        processRegistry.registerPort(startPortCopy)

        return startPortCopy
    }
}