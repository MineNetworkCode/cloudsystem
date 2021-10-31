package systems.beemo.cloudsystem.worker.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.network.protocol.Packet

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelActive(channelHandlerContext: ChannelHandlerContext) {
        logger.info("Connected to master") // TODO: Better message and handle connection to master
    }

    @Throws(Exception::class)
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
    }
}