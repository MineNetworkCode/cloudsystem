package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.DownloadUtils
import java.io.File
import java.net.SocketTimeoutException

class BungeeDownloadConfiguration : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(BungeeDownloadConfiguration::class.java)

    override fun execute() {
        val bungeeJar = File(DirectoryConstants.MASTER_LOCAL_BUNGEE, "bungeecord.jar")
        if (bungeeJar.exists()) return

        logger.info("BungeeCord was not found in the cloud files. Starting download... the time depends on the servers connection!")
        try {
            // TODO: Download chosen version of user
            DownloadUtils.downloadFile(
                "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
                bungeeJar.path,
                6000,
                6000,
                false
            )
        } catch (e: SocketTimeoutException) {
            logger.error("Could not download the bungeecord version. Is the download server down?")
        }
    }
}