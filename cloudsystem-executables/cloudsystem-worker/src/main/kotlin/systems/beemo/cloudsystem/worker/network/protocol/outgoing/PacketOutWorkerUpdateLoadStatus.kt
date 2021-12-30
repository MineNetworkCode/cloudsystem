package systems.beemo.cloudsystem.worker.network.protocol.outgoing

import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet

class PacketOutWorkerUpdateLoadStatus(
    private val uuid: String,
    private val currentOnlineServers: Int,
    private val currentMemoryConsumption: Long,
    private val currentCpuConsumption: Double
) : Packet {

    constructor() : this("empty", 0, -1L, -1.0)

    override fun write(): Document {
        return Document().appendString("uuid", uuid)
            .appendInt("currentOnlineServers", currentOnlineServers)
            .appendLong("currentMemoryConsumption", currentMemoryConsumption)
            .appendDouble("currentCpuConsumption", currentCpuConsumption)
    }
}