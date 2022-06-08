package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.master.groups.bungee.BungeeGroupHandler
import systems.beemo.cloudsystem.master.groups.bungee.models.BungeeGroup
import java.io.File

class BungeeGroupLoader(
    private val bungeeGroupHandler: BungeeGroupHandler
) : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(BungeeGroupLoader::class.java)

    override fun execute() {
        val bungeeGroupFiles = File(DirectoryConstants.MASTER_CONFIG_GROUPS_BUNGEE).listFiles()

        if (bungeeGroupFiles == null || bungeeGroupFiles.isEmpty()) {
            val bungeeGroup = BungeeGroup(
                name = "Bungee",
                maxServersOnline = 1,
                minServersOnline = 1,
                maxMemory = 512,
                minMemory = 128,
                maxPlayers = 1000,
                joinPower = 0,
                maintenance = false
            )

            bungeeGroupHandler.createGroup(bungeeGroup)

            logger.warn("Cannot find any bungee groups to load. Created the default one!")
            return
        }

        for (bungeeGroupFile in bungeeGroupFiles) {
            if (!bungeeGroupFile.name.endsWith(".json")) continue

            val document = Document.read(bungeeGroupFile)
            val bungeeGroup = BungeeGroup.fromDocument(document)

            bungeeGroupHandler.registerGroup(bungeeGroup)
        }
    }
}