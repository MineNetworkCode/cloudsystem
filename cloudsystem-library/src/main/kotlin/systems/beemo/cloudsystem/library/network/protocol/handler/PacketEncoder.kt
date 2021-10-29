package systems.beemo.cloudsystem.library.network.protocol.handler

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.network.protocol.exceptions.PacketNotFoundException

class PacketEncoder(
    private val packetRegistry: PacketRegistry
) : MessageToByteEncoder<Packet>() {

    override fun encode(channelHandlerContext: ChannelHandlerContext, packet: Packet, byteBuf: ByteBuf) {
        val packetId = packetRegistry.getIdByOutgoingPacket(packet)

        if (packetId == -1) throw PacketNotFoundException("ID of Packet(${packet::class.java.simpleName}) was not found!")
        else {
            byteBuf.writeInt(packetId)

            val byteBufOutputStream = ByteBufOutputStream(byteBuf)
            val document = packet.write()

            byteBufOutputStream.writeUTF(document.getAsString())
        }
    }
}