package systems.beemo.cloudsystem.master.network

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

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        logger.info("Some worker disconnected") // TODO: Better message and handle disconnecting worker
    }

    @Throws(Exception::class)
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
    }
}