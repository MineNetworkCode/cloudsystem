package systems.beemo.cloudsystem.master.network

import io.netty.channel.Channel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.ssl.SslContext
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.network.protocol.handler.PacketDecoder
import systems.beemo.cloudsystem.library.network.protocol.handler.PacketEncoder
import systems.beemo.cloudsystem.library.network.server.NetworkServer

class NetworkServerImpl(
    nettyHelper: NettyHelper,
    private val packetRegistry: PacketRegistry
) : NetworkServer(nettyHelper) {

    override fun preparePipeline(sslContext: SslContext?, channel: Channel) {
        if (sslContext != null) channel.pipeline().addLast(sslContext.newHandler(channel.alloc()))

        channel.pipeline()
            .addLast(LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
            .addLast(PacketDecoder(packetRegistry))
            .addLast(LengthFieldPrepender(4))
            .addLast(PacketEncoder(packetRegistry))
            .addLast(NetworkHandler())
    }
}