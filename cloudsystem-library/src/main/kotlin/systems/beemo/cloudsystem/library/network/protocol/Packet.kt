package systems.beemo.cloudsystem.library.network.protocol

import io.netty.channel.ChannelHandlerContext
import systems.beemo.cloudsystem.library.document.Document

interface Packet {

    fun read(document: Document) {}
    fun write(): Document = Document().appendString("message", "no_data")
    fun handle(channelHandlerContext: ChannelHandlerContext)
}