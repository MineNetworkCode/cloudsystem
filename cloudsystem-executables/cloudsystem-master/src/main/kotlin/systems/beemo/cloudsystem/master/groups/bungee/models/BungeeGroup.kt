package systems.beemo.cloudsystem.master.groups.bungee.models

import systems.beemo.cloudsystem.library.document.Document

data class BungeeGroup(
    val name: String,
    var maxServersOnline: Int,
    var minServersOnline: Int,
    var maxMemory: Int,
    var minMemory: Int,
    var maxPlayers: Int,
    var joinPower: Int,
    var maintenance: Boolean,
) {

    companion object {
        fun toDocument(bungeeGroup: BungeeGroup): Document {
            return Document().appendString("name", bungeeGroup.name)
                .appendInt("maxServersOnline", bungeeGroup.maxServersOnline)
                .appendInt("minServersOnline", bungeeGroup.minServersOnline)
                .appendInt("maxMemory", bungeeGroup.maxMemory)
                .appendInt("minMemory", bungeeGroup.minMemory)
                .appendInt("maxPlayers", bungeeGroup.maxPlayers)
                .appendInt("joinPower", bungeeGroup.joinPower)
                .appendBoolean("maintenance", bungeeGroup.maintenance)
        }

        fun fromDocument(document: Document): BungeeGroup {
            return BungeeGroup(
                name = document.getStringValue("name"),
                maxServersOnline = document.getIntValue("maxServersOnline"),
                minServersOnline = document.getIntValue("minServersOnline"),
                maxMemory = document.getIntValue("maxMemory"),
                minMemory = document.getIntValue("minMemory"),
                maxPlayers = document.getIntValue("maxPlayers"),
                joinPower = document.getIntValue("joinPower"),
                maintenance = document.getBooleanValue("maintenance")
            )
        }
    }
}