package systems.beemo.cloudsystem.master.configuration

import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.master.CloudSystemMaster
import java.io.File
import java.util.*

class WebKeyCreator : Configuration {

    override fun execute() {
        val webKeyFile = File("${DirectoryConstants.MASTER_SECURE}/web.key")

        if(!webKeyFile.exists()) {
            val webKey = StringBuilder()

            (0..10).forEach { _ ->
                webKey.append(UUID.randomUUID().toString().replace("-", ""))
            }

            CloudSystemMaster.WEB_KEY = webKey.toString()
            FileUtils.writeStringToFile(webKeyFile, webKey.toString())
        } else {
            CloudSystemMaster.WEB_KEY = FileUtils.readStringFromFile(webKeyFile)
        }
    }
}