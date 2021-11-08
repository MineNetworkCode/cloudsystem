package systems.beemo.cloudsystem.library.network.protocol

class PacketRegistry {

    private val incomingPackets: MutableMap<Int, Class<out Packet>> = mutableMapOf()

    private val outgoingPackets: MutableMap<Int, Class<out Packet>> = mutableMapOf()

    fun getIncomingPacketById(packetId: Int): Packet? {
        return this.incomingPackets[packetId]?.getDeclaredConstructor()?.newInstance()
    }

    fun getIdByOutgoingPacket(packet: Packet): Int {
        var returnValue = -1

        for (entry in outgoingPackets) {
            if (entry.value != packet::class.java) continue
            returnValue = entry.key
        }

        return returnValue
    }

    fun registerIncomingPacket(packetId: PacketId, packetClass: Class<out Packet>) {
        this.incomingPackets[packetId.packetId] = packetClass
    }

    fun registerOutgoingPacket(packetId: PacketId, packetClass: Class<out Packet>) {
        this.outgoingPackets[packetId.packetId] = packetClass
    }
}