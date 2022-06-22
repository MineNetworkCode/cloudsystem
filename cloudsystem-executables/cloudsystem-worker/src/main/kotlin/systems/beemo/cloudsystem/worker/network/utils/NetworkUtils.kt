package systems.beemo.cloudsystem.worker.network.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import systems.beemo.cloudsystem.library.network.protocol.Packet

class NetworkUtils {

    fun sendPacket(packet: Packet, channel: Channel): ChannelFuture? {
        return channel.writeAndFlush(packet)
    }
}