package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.DownloadUtils
import java.io.File
import java.net.SocketTimeoutException
import kotlin.system.exitProcess

class SpigotDownloadConfiguration : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(SpigotDownloadConfiguration::class.java)

    private val downloadLinks: MutableMap<String, String> = mutableMapOf(
        "SPIGOT_1_18_2" to "https://download.getbukkit.org/spigot/spigot-1.18.2.jar",
        "PAPER_1_18_2" to "https://api.papermc.io/v2/projects/paper/versions/1.18.2/builds/379/downloads/paper-1.18.2-379.jar"
    )

    override fun execute() {
        val spigotJar = File(DirectoryConstants.MASTER_LOCAL_SPIGOT, "spigot.jar")
        if (spigotJar.exists()) return

        val downloadLink = this.readSpigotVersion()

        logger.info("Alright, starting to download your requested spigot version!")

        try {
            DownloadUtils.downloadFile(
                downloadLink,
                spigotJar.path,
                6000,
                6000,
                false
            )
        } catch (e: SocketTimeoutException) {
            logger.error("Could not download the spigot version. Is the download server down?")
            logger.error("Used following download link: $downloadLink")
            exitProcess(0)
        }
    }

    private fun readSpigotVersion(): String {
        val availableVersions = this.downloadLinks.keys

        logger.info("Please choose a spigot version. $availableVersions")
        val input = bufferedReader.readLine()

        if (!(availableVersions.contains(input.uppercase()))) {
            return this.readSpigotVersion()
        }

        return this.downloadLinks[input.uppercase()]!!
    }
}