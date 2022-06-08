package systems.beemo.cloudsystem.master.network.protocol.outgoing.worker

import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet

class PacketOutWorkerConnectionEstablished(
    private val message: String,
    private val webKey: String,
    private val successful: Boolean
) : Packet {

    constructor() : this("empty", "empty", false)

    override fun write(): Document {
        return Document().appendString("message", message)
            .appendString("webKey", webKey)
            .appendBoolean("successful", successful)
    }
}