package systems.beemo.cloudsystem.master.process.handler.handlers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType
import systems.beemo.cloudsystem.master.groups.bungee.BungeeGroupHandler
import systems.beemo.cloudsystem.master.network.utils.NetworkUtils
import systems.beemo.cloudsystem.master.process.handler.ProcessRequestHandler
import systems.beemo.cloudsystem.master.process.models.BungeeProcess
import systems.beemo.cloudsystem.master.process.registry.BungeeProcessRegistry
import systems.beemo.cloudsystem.master.worker.WorkerRegistry
import java.util.*

class BungeeProcessRequestHandler(
    private val networkUtils: NetworkUtils,
    private val bungeeProcessRegistry: BungeeProcessRegistry,
    private val workerRegistry: WorkerRegistry,
    private val bungeeGroupHandler: BungeeGroupHandler
) : ProcessRequestHandler<BungeeProcess> {

    private val logger: Logger = LoggerFactory.getLogger(BungeeProcessRequestHandler::class.java)

    override fun requestProcessesOnConnect(workerInfo: WorkerInfo) {
        for (bungeeGroup in workerInfo.responsibleGroups.stream().filter { bungeeGroupHandler.getGroup(it) != null }
            .map { bungeeGroupHandler.getGroup(it) }) {
            if (bungeeGroup == null) {
                logger.error("An error occurred while requesting the processes on connect of Worker:(Name=" + workerInfo.name + ")")
                continue
            }

            val currentlyRunning = bungeeProcessRegistry.getRunningProcessCount(bungeeGroup.name)
            val name = "${bungeeGroup.name}-${if ((currentlyRunning + 1) >= 10) "$currentlyRunning" else "0$currentlyRunning"}"

            val workerChannel = workerInfo.channel

            if (workerChannel == null) {
                logger.error("Somehow the workers channel is null... Did the worker crash?")
                continue
            }

            val ip = workerChannel.remoteAddress().toString().replace("/", "").split(":")[0]

            if (bungeeGroup.minServersOnline != 1) {
                if (currentlyRunning >= bungeeGroup.minServersOnline) continue

                val count = bungeeGroup.minServersOnline - currentlyRunning
                val memoryUsageAfterRequest = workerInfo.currentMemoryConsumption + (bungeeGroup.maxMemory * count)

                if (memoryUsageAfterRequest > workerInfo.memory) {
                    logger.error("Could not handle the request. It would consume too much memory!")
                    continue
                }

                this.requestMultipleProcesses(
                    count,
                    BungeeProcess(
                        groupName = bungeeGroup.name,
                        name = name,
                        uuid = UUID.randomUUID().toString(),
                        ip = ip,
                        type = ProcessType.BUNGEE,
                        stage = ProcessStage.STARTING,
                        minMemory = bungeeGroup.minMemory,
                        maxMemory = bungeeGroup.maxMemory,
                        port = 25565, // TODO: Dynamic Port Detection!
                        maxPlayers = bungeeGroup.maxPlayers,
                        joinPower = bungeeGroup.joinPower,
                        maintenance = bungeeGroup.maintenance
                    )
                )
            } else {
                if (currentlyRunning >= bungeeGroup.minServersOnline) {
                    continue
                }

                this.requestProcess(
                    BungeeProcess(
                        groupName = bungeeGroup.name,
                        name = name,
                        uuid = UUID.randomUUID().toString(),
                        ip = ip,
                        type = ProcessType.BUNGEE,
                        stage = ProcessStage.STARTING,
                        minMemory = bungeeGroup.minMemory,
                        maxMemory = bungeeGroup.maxMemory,
                        port = 25565, // TODO: Dynamic Port Detection!
                        maxPlayers = bungeeGroup.maxPlayers,
                        joinPower = bungeeGroup.joinPower,
                        maintenance = bungeeGroup.maintenance
                    )
                )
            }
        }
    }

    override fun requestMultipleProcesses(count: Int, process: BungeeProcess) {
        (1..count).forEach { _ ->
            this.requestProcess(process)
        }
    }

    override fun requestProcess(process: BungeeProcess) {
        val groupName = process.groupName

        val workerInfo = workerRegistry.getLeastUsedWorker(groupName)

        if (workerInfo == null) {
            logger.error("Could not find any usable worker for BungeeProcess:(Name=${process.name})")
            return
        }

        val workerChannel = workerInfo.channel

        if (workerChannel == null) {
            logger.error("Somehow the workers channel is null... Did the worker crash?")
            return
        }

        //networkUtils.sendPacket(PacketOutRequestProcess(process), workerChannel)

        bungeeProcessRegistry.registerProcess(process)
        logger.info("Requested new BungeeProcess:(Name=${process.name})")
    }
}