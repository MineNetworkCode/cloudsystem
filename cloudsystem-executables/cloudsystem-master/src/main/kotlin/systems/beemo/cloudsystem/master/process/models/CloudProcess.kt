package systems.beemo.cloudsystem.master.process.models

import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType

data class CloudProcess(
    val groupName: String,
    val name: String,
    val uuid: String,
    val ip: String,
    val type: ProcessType,
    val stage: ProcessStage,
    val minMemory: Int,
    val maxMemory: Int,
    val port: Int,
    val maxPlayers: Int,
    val joinPower: Int,
    val maintenance: Boolean,
    val lobbyServer: Boolean = false,
    val dynamicServer: Boolean = false,
    val staticServer: Boolean = false,
)
