package systems.beemo.cloudsystem.worker.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.error.ErrorHandler
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.network.protocol.out.PacketOutWorkerRequestConnection
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val networkUtils: NetworkUtils by CloudSystemWorker.KODEIN.instance()
    private val errorHandler: ErrorHandler by CloudSystemWorker.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        networkUtils.sendPacket(
            PacketOutWorkerRequestConnection(
                secretKey = CloudSystemWorker.RUNTIME_VARS.secretKey,
                workerInfo = WorkerInfo(
                    uuid = CloudSystemWorker.RUNTIME_VARS.workerConfig.workerUuid,
                    name = CloudSystemWorker.RUNTIME_VARS.workerConfig.workerName,
                    delimiter = CloudSystemWorker.RUNTIME_VARS.workerConfig.delimiter,
                    suffix = CloudSystemWorker.RUNTIME_VARS.workerConfig.suffix,
                    currentOnlineServers = 0,
                    memory = CloudSystemWorker.RUNTIME_VARS.workerConfig.memory,
                    currentMemoryConsumption = HardwareUtils.getMemoryUsage(),
                    currentCpuConsumption = HardwareUtils.getCpuUsage(),
                    responsibleGroups = CloudSystemWorker.RUNTIME_VARS.workerConfig.responsibleGroups
                )
            ), channelHandlerContext.channel()
        )

        CloudSystemWorker.RUNTIME_VARS.masterChannel = channelHandlerContext.channel()
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        errorHandler.handleError(null, cause)
    }
}