package systems.beemo.cloudsystem.master.process.models

import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType

open class BungeeProcess(
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
) : AbstractProcess(groupName, name, uuid, ip, type, stage, minMemory, maxMemory, port, maxPlayers, joinPower, maintenance)