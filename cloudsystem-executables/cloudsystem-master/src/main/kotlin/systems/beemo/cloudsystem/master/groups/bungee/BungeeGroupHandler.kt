package systems.beemo.cloudsystem.master.groups.bungee

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.cache.Cache
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.library.utils.ZipUtils
import systems.beemo.cloudsystem.master.groups.bungee.models.BungeeGroup
import java.io.File
import java.nio.file.Path

class BungeeGroupHandler {

    private val logger: Logger = LoggerFactory.getLogger(BungeeGroupHandler::class.java)

    private val bungeeGroups: Cache<String, BungeeGroup> = Cache()

    fun registerGroup(bungeeGroup: BungeeGroup) {
        if (bungeeGroups.containsKey(bungeeGroup.name)) return

        this.bungeeGroups[bungeeGroup.name] = bungeeGroup
        logger.info("Successfully registered BungeeGroup:(Name=${bungeeGroup.name})")
    }

    fun createGroup(bungeeGroup: BungeeGroup) {
        val document = BungeeGroup.toDocument(bungeeGroup)
        document.write(File(DirectoryConstants.MASTER_CONFIG_GROUPS_BUNGEE, "${bungeeGroup.name}.json"))

        this.bungeeGroups[bungeeGroup.name] = bungeeGroup

        val templatePath = Path.of("${DirectoryConstants.MASTER_TEMPLATE_BUNGEE}/${bungeeGroup.name}")
        val defaultTemplatePath = Path.of("${DirectoryConstants.MASTER_TEMPLATE_BUNGEE}/${bungeeGroup.name}/default/")

        FileUtils.createDirectory(templatePath)
        FileUtils.createDirectory(defaultTemplatePath)
        FileUtils.createDirectory(File(defaultTemplatePath.toFile(), "plugins").toPath())
        FileUtils.copyFile(File(DirectoryConstants.MASTER_LOCAL_BUNGEE, "bungeecord.jar"), File(defaultTemplatePath.toFile(), "bungeecord.jar"))
        FileUtils.copyAllFiles(File(DirectoryConstants.MASTER_GLOBAL_BUNGEE).toPath(), File(defaultTemplatePath.toFile(), "plugins").path)

        FileUtils.copyAllFiles(templatePath, "${DirectoryConstants.MASTER_WEB}/${bungeeGroup.name}")
        ZipUtils.zipFiles(
            File("${DirectoryConstants.MASTER_WEB}/${bungeeGroup.name}"),
            File("${DirectoryConstants.MASTER_WEB}/${bungeeGroup.name}.zip")
        )
        FileUtils.deleteFullDirectory("${DirectoryConstants.MASTER_WEB}/${bungeeGroup.name}")

        logger.info("Created and loaded BungeeGroup:(Name=${bungeeGroup.name})")
    }

    fun deleteGroup(bungeeGroup: BungeeGroup) {
        this.bungeeGroups.remove(bungeeGroup.name)
        FileUtils.deleteIfExists(File(DirectoryConstants.MASTER_CONFIG_GROUPS_BUNGEE, "${bungeeGroup.name}.json"))
        logger.info("Deleted and unloaded BungeeGroup:(Name=${bungeeGroup.name})")
    }

    fun editGroup(bungeeGroup: BungeeGroup) {
        this.deleteGroup(bungeeGroup)
        this.createGroup(bungeeGroup)
    }

    fun getGroup(name: String): BungeeGroup? {
        return this.bungeeGroups[name]
    }
}