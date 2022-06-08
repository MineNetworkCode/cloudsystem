package systems.beemo.cloudsystem.master.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import systems.beemo.cloudsystem.library.network.protocol.Packet

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        // TODO: Unregister Worker
    }

    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
        // TODO: Custom Errorhandler
    }
}