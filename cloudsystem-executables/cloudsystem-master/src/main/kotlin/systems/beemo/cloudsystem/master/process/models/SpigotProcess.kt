package systems.beemo.cloudsystem.master.process.models

import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType

open class SpigotProcess(
    groupName: String,
    name: String,
    uuid: String,
    ip: String,
    type: ProcessType,
    stage: ProcessStage,
    minMemory: Int,
    maxMemory: Int,
    port: Int,
    maxPlayers: Int,
    joinPower: Int,
    maintenance: Boolean,
    val lobbyServer: Boolean,
    val dynamicServer: Boolean,
    val staticServer: Boolean,
) : AbstractProcess(groupName, name, uuid, ip, type, stage, minMemory, maxMemory, port, maxPlayers, joinPower, maintenance)