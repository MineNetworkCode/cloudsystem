package systems.beemo.cloudsystem.master.groups.spigot.models

import systems.beemo.cloudsystem.library.document.Document

data class SpigotGroup(
    val name: String,
    val template: String,
    var maxServersOnline: Int,
    var minServersOnline: Int,
    var maxMemory: Int,
    var minMemory: Int,
    var maxPlayers: Int,
    var newServerPercentage: Int,
    var joinPower: Int,
    var maintenance: Boolean,
    var lobbyServer: Boolean,
    var dynamicServer: Boolean,
    var staticServer: Boolean,
    var randomTemplateMode: Boolean,
    var templateModes: MutableList<String>,
) {

    companion object {
        fun toDocument(spigotGroup: SpigotGroup): Document {
            return Document().appendString("name", spigotGroup.name)
                .appendString("template", spigotGroup.template)
                .appendInt("maxServersOnline", spigotGroup.maxServersOnline)
                .appendInt("minServersOnline", spigotGroup.minServersOnline)
                .appendInt("maxMemory", spigotGroup.maxMemory)
                .appendInt("minMemory", spigotGroup.minMemory)
                .appendInt("maxPlayers", spigotGroup.maxPlayers)
                .appendInt("newServerPercentage", spigotGroup.newServerPercentage)
                .appendInt("joinPower", spigotGroup.joinPower)
                .appendBoolean("maintenance", spigotGroup.maintenance)
                .appendBoolean("lobbyServer", spigotGroup.lobbyServer)
                .appendBoolean("dynamicServer", spigotGroup.dynamicServer)
                .appendBoolean("staticServer", spigotGroup.staticServer)
                .appendBoolean("randomTemplateMode", spigotGroup.randomTemplateMode)
                .appendList("templateModes", spigotGroup.templateModes)
        }

        fun fromDocument(document: Document): SpigotGroup {
            return SpigotGroup(
                name = document.getStringValue("name"),
                template = document.getStringValue("template"),
                maxServersOnline = document.getIntValue("maxServersOnline"),
                minServersOnline = document.getIntValue("minServersOnline"),
                maxMemory = document.getIntValue("maxMemory"),
                minMemory = document.getIntValue("minMemory"),
                maxPlayers = document.getIntValue("maxPlayers"),
                newServerPercentage = document.getIntValue("newServerPercentage"),
                joinPower = document.getIntValue("joinPower"),
                maintenance = document.getBooleanValue("maintenance"),
                lobbyServer = document.getBooleanValue("lobbyServer"),
                dynamicServer = document.getBooleanValue("dynamicServer"),
                staticServer = document.getBooleanValue("staticServer"),
                randomTemplateMode = document.getBooleanValue("randomTemplateMode"),
                templateModes = document.getList("templateModes") as MutableList<String>
            )
        }
    }
}