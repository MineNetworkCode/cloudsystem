package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.DownloadUtils
import java.io.File
import java.net.SocketTimeoutException

class SpigotDownloadConfiguration : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(SpigotDownloadConfiguration::class.java)

    override fun execute() {
        val spigotJar = File(DirectoryConstants.MASTER_LOCAL_SPIGOT, "spigot.jar")
        if (spigotJar.exists()) return

        logger.info("Spigot was not found in the cloud files. Starting download... the time depends on the servers connection!")
        try {
            // TODO: Download chosen version of user
            DownloadUtils.downloadFile(
                "https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar",
                spigotJar.path,
                6000,
                6000,
                false
            )
        } catch (e: SocketTimeoutException) {
            logger.error("Could not download the spigot version. Is the download server down?")
        }
    }
}