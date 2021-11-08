package systems.beemo.cloudsystem.worker.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.network.protocol.outgoing.PacketOutWorkerRequestConnection
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils
import java.util.*

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val networkUtils: NetworkUtils by CloudSystemWorker.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        networkUtils.sendPacketAsync(
            PacketOutWorkerRequestConnection(
                "yey",
                WorkerInfo(
                    UUID.randomUUID().toString(), "Worker", "-", "01",
                    HardwareUtils.getSystemMemory(),
                    HardwareUtils.getMemoryUsage(),
                    HardwareUtils.getCpuUsage(),
                    mutableListOf("Lobby", "Proxy")
                )
            ), channelHandlerContext.channel()
        )
    }

    @Throws(Exception::class)
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
    }
}