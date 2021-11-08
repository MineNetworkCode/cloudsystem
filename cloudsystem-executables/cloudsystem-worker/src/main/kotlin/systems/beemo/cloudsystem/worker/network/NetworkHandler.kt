package systems.beemo.cloudsystem.worker.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.network.protocol.outgoing.PacketOutWorkerRequestConnection
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val networkUtils: NetworkUtils by CloudSystemWorker.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        networkUtils.sendPacketAsync(
            PacketOutWorkerRequestConnection(
                secretKey = "yey",
                workerInfo = WorkerInfo(
                    uuid = CloudSystemWorker.WORKER_CONFIG.workerUuid,
                    name = CloudSystemWorker.WORKER_CONFIG.workerName,
                    delimiter = CloudSystemWorker.WORKER_CONFIG.delimiter,
                    suffix = CloudSystemWorker.WORKER_CONFIG.suffix,
                    memory = CloudSystemWorker.WORKER_CONFIG.memory,
                    currentMemoryConsumption = HardwareUtils.getMemoryUsage(),
                    currentCpuConsumption = HardwareUtils.getCpuUsage(),
                    responsibleGroups = CloudSystemWorker.WORKER_CONFIG.responsibleGroups
                )
            ), channelHandlerContext.channel()
        )
    }

    @Throws(Exception::class)
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
    }
}