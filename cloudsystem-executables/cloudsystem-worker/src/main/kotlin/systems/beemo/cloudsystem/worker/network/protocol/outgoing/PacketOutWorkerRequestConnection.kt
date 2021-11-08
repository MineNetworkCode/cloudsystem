package systems.beemo.cloudsystem.worker.network.protocol.outgoing

import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.protocol.Packet

class PacketOutWorkerRequestConnection(
    private val secretKey: String,
    private val workerInfo: WorkerInfo
) : Packet {

    constructor() : this(
        "empty",
        WorkerInfo(
            "empty", "empty", "empty", "empty", -1L,
            -1L, -1.0, mutableListOf()
        )
    )

    override fun write(): Document {
        return Document().appendString("secretKey", secretKey)
            .appendDocument("workerInfo", WorkerInfo.toDocument(workerInfo))
    }
}