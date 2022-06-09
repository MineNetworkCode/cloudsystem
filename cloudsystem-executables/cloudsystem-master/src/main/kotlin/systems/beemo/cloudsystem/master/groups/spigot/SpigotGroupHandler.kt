package systems.beemo.cloudsystem.master.groups.spigot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.cache.Cache
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.library.utils.ZipUtils
import systems.beemo.cloudsystem.master.groups.spigot.models.SpigotGroup
import java.io.File
import java.nio.file.Path

class SpigotGroupHandler {

    private val logger: Logger = LoggerFactory.getLogger(SpigotGroupHandler::class.java)

    private val spigotGroups: Cache<String, SpigotGroup> = Cache()

    fun registerGroup(spigotGroup: SpigotGroup) {
        if (spigotGroups.containsKey(spigotGroup.name)) return

        this.spigotGroups[spigotGroup.name] = spigotGroup
        logger.info("Successfully registered SpigotGroup:(Name=${spigotGroup.name})")
    }

    fun createGroup(spigotGroup: SpigotGroup) {
        val document = SpigotGroup.toDocument(spigotGroup)
        document.write(File(DirectoryConstants.MASTER_CONFIG_GROUPS_SPIGOT, "${spigotGroup.name}.json"))

        val templatePath = Path.of("${DirectoryConstants.MASTER_TEMPLATE_SPIGOT}/${spigotGroup.name}")
        val defaultTemplatePath = Path.of("${DirectoryConstants.MASTER_TEMPLATE_SPIGOT}/${spigotGroup.name}/default/")

        FileUtils.createDirectory(templatePath)
        FileUtils.createDirectory(defaultTemplatePath)
        FileUtils.createDirectory(File(defaultTemplatePath.toFile(), "plugins").toPath())
        FileUtils.copyFile(File(DirectoryConstants.MASTER_LOCAL_SPIGOT, "spigot.jar"), File(defaultTemplatePath.toFile(), "spigot.jar"))
        FileUtils.copyAllFiles(File(DirectoryConstants.MASTER_GLOBAL_SPIGOT).toPath(), File(defaultTemplatePath.toFile(), "plugins").path)

        FileUtils.copyAllFiles(templatePath, "${DirectoryConstants.MASTER_WEB}/${spigotGroup.name}")
        ZipUtils.zipFiles(
            File("${DirectoryConstants.MASTER_WEB}/${spigotGroup.name}"),
            File("${DirectoryConstants.MASTER_WEB}/${spigotGroup.name}.zip")
        )
        FileUtils.deleteFullDirectory("${DirectoryConstants.MASTER_WEB}/${spigotGroup.name}")

        this.spigotGroups[spigotGroup.name] = spigotGroup
        logger.info("Created and loaded SpigotGroup:(Name=${spigotGroup.name})")
    }

    fun deleteGroup(spigotGroup: SpigotGroup) {
        this.spigotGroups.remove(spigotGroup.name)
        FileUtils.deleteIfExists(File(DirectoryConstants.MASTER_CONFIG_GROUPS_SPIGOT, "${spigotGroup.name}.json"))
        logger.info("Deleted and unloaded SpigotGroup:(Name=${spigotGroup.name})")
    }

    fun editGroup(spigotGroup: SpigotGroup) {
        this.deleteGroup(spigotGroup)
        this.createGroup(spigotGroup)
    }

    fun getGroup(name: String): SpigotGroup? {
        return this.spigotGroups[name]
    }
}