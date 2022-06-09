package systems.beemo.cloudsystem.worker.configuration

import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import java.nio.file.Path

class DefaultFolderCreator : Configuration() {

    private val requiredFolders: MutableList<Path> = mutableListOf(
        Path.of(DirectoryConstants.WORKER),
        Path.of(DirectoryConstants.WORKER_CACHED),
        Path.of(DirectoryConstants.WORKER_CACHED_TEMPLATES),
        Path.of(DirectoryConstants.WORKER_CACHED_TEMPLATES_BUNGEE),
        Path.of(DirectoryConstants.WORKER_CACHED_TEMPLATES_SPIGOT),
        Path.of(DirectoryConstants.WORKER_SECURE),
        Path.of(DirectoryConstants.WORKER_CONFIG),
        Path.of(DirectoryConstants.WORKER_RUNNING)
    )

    override fun execute() {
        for (requiredFolder in requiredFolders) {
            if (requiredFolder.toFile().exists()) continue

            FileUtils.createDirectory(requiredFolder)
        }
    }
}