package systems.beemo.cloudsystem.master.configuration

import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import java.nio.file.Path

class DefaultFolderCreator : Configuration {

    private val requiredFolders: MutableList<Path> = mutableListOf(
        Path.of(DirectoryConstants.MASTER),
        Path.of(DirectoryConstants.MASTER_SECURE),
        Path.of(DirectoryConstants.MASTER_DATA),
        Path.of(DirectoryConstants.MASTER_DATA_CLOUD_PLAYERS),
        Path.of(DirectoryConstants.MASTER_CONFIG),
        Path.of(DirectoryConstants.MASTER_CONFIG_CLOUD),
        Path.of(DirectoryConstants.MASTER_CONFIG_GROUPS),
        Path.of(DirectoryConstants.MASTER_CONFIG_GROUPS_PROXY),
        Path.of(DirectoryConstants.MASTER_CONFIG_GROUPS_SERVER),
        Path.of(DirectoryConstants.MASTER_CONFIG_PERMISSIONS),
        Path.of(DirectoryConstants.MASTER_ADDONS),
        Path.of(DirectoryConstants.MASTER_TEMPLATE),
        Path.of(DirectoryConstants.MASTER_TEMPLATE_PROXY),
        Path.of(DirectoryConstants.MASTER_TEMPLATE_SERVER),
        Path.of(DirectoryConstants.MASTER_WEB),
        Path.of(DirectoryConstants.MASTER_GLOBAL),
        Path.of(DirectoryConstants.MASTER_GLOBAL_PROXY),
        Path.of(DirectoryConstants.MASTER_GLOBAL_SERVER),
        Path.of(DirectoryConstants.MASTER_LOCAL),
        Path.of(DirectoryConstants.MASTER_LOCAL_SERVER),
        Path.of(DirectoryConstants.MASTER_LOCAL_PROXY)
    )

    override fun execute() {
        for (requiredFolder in requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}