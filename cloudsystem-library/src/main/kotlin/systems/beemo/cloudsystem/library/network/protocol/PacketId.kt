package systems.beemo.cloudsystem.library.network.protocol

enum class PacketId(val packetId: Int) {

    PACKET_REQUEST_CONNECTION(1),
    PACKET_ESTABLISHED_CONNECTION(2),
    PACKET_UPDATE_LOAD_STATUS(3),
}