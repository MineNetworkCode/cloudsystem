package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.DownloadUtils
import java.io.File
import kotlin.system.exitProcess

class BungeeDownloadConfiguration : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(BungeeDownloadConfiguration::class.java)

    private val downloadLinks: MutableMap<String, String> = mutableMapOf(
        "BUNGEECORD" to "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar",
        "WATERFALL" to "https://api.papermc.io/v2/projects/waterfall/versions/1.18/builds/488/downloads/waterfall-1.18-488.jar"
    )

    override fun execute() {
        val bungeeJar = File(DirectoryConstants.MASTER_LOCAL_BUNGEE, "bungeecord.jar")
        if (bungeeJar.exists()) return

        val downloadLink = this.readBungeeVersion()

        try {
            DownloadUtils.downloadFile(
                downloadLink,
                bungeeJar.path,
                6000,
                6000,
                false
            )
        } catch (e: Exception) {
            logger.error("Could not download the bungeecord version. Is the download server down?")
            logger.error("Used following download link: $downloadLink")
            exitProcess(0)
        }
    }

    private fun readBungeeVersion(): String {
        val availableVersions = this.downloadLinks.keys

        logger.info("Please choose a bungeecord version. $availableVersions")
        val input = bufferedReader.readLine()

        if (!(availableVersions.contains(input.uppercase()))) {
            return this.readBungeeVersion()
        }

        return this.downloadLinks[input.uppercase()]!!
    }
}