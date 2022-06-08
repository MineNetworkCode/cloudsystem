package systems.beemo.cloudsystem.master.network.protocol.outgoing.process

import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType
import systems.beemo.cloudsystem.master.process.models.CloudProcess

class PacketOutRequestProcess(
    private val cloudProcess: CloudProcess
) : Packet {

    constructor() : this(
        CloudProcess(
            groupName = "empty",
            name = "empty",
            uuid = "empty",
            ip = "empty",
            type = ProcessType.SPIGOT,
            stage = ProcessStage.STARTING,
            maxMemory = -1,
            minMemory = -1,
            port = -1,
            maxPlayers = -1,
            joinPower = -1,
            maintenance = false
        )
    )

    override fun write(): Document {
        val document = Document().appendString("groupName", cloudProcess.groupName)
            .appendString("name", cloudProcess.name)
            .appendString("uuid", cloudProcess.uuid)
            .appendString("ip", cloudProcess.ip)
            .appendString("type", cloudProcess.type.toString())
            .appendString("stage", cloudProcess.stage.toString())
            .appendInt("maxMemory", cloudProcess.maxMemory)
            .appendInt("minMemory", cloudProcess.minMemory)
            .appendInt("port", cloudProcess.port)
            .appendInt("maxPlayers", cloudProcess.maxPlayers)
            .appendInt("joinPower", cloudProcess.joinPower)
            .appendBoolean("maintenance", cloudProcess.maintenance)

        if (cloudProcess.type == ProcessType.SPIGOT) {
            document.appendBoolean("lobbyServer", cloudProcess.lobbyServer)
                .appendBoolean("dynamicServer", cloudProcess.dynamicServer)
                .appendBoolean("staticServer", cloudProcess.staticServer)
        }

        return document
    }
}