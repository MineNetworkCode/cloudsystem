package systems.beemo.cloudsystem.master.network.protocol.incoming

import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.worker.WorkerRegistry
import kotlin.properties.Delegates

class PacketInWorkerUpdateLoadStatus : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInWorkerUpdateLoadStatus::class.java)

    private val workerRegistry: WorkerRegistry by CloudSystemMaster.KODEIN.instance()

    lateinit var uuid: String

    var currentOnlineServers by Delegates.notNull<Int>()
    var currentMemoryConsumption by Delegates.notNull<Long>()
    var currentCpuConsumption by Delegates.notNull<Double>()

    override fun read(document: Document) {
        uuid = document.getStringValue("uuid")
        currentOnlineServers = document.getIntValue("currentOnlineServers")
        currentMemoryConsumption = document.getLongValue("currentMemoryConsumption")
        currentCpuConsumption = document.getDoubleValue("currentCpuConsumption")
    }

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        val workerInfo = workerRegistry.getWorker(uuid)

        if (workerInfo == null) {
            logger.error("Internal error occurred while updating load status!")
            return
        }

        workerInfo.currentOnlineServers = currentOnlineServers
        workerInfo.currentMemoryConsumption = currentMemoryConsumption
        workerInfo.currentCpuConsumption = currentCpuConsumption

        workerRegistry.updateWorker(uuid, workerInfo)
    }
}