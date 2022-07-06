package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.master.CloudSystemMaster
import java.io.File
import java.util.*

class KeysCreator : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(KeysCreator::class.java)

    override fun execute() {
        val workerKeyFile = File("${DirectoryConstants.MASTER_SECURE}/worker.key")

        if (!workerKeyFile.exists()) {
            val workerKey: String = this.generateKey();

            CloudSystemMaster.RUNTIME_VARS.secretKey = workerKey
            FileUtils.writeStringToFile(workerKeyFile, workerKey)

            logger.warn("It seems the worker key was just created. Please copy it to the \"${DirectoryConstants.WORKER_SECURE}\" folder!")
        } else {
            CloudSystemMaster.RUNTIME_VARS.secretKey = FileUtils.readStringFromFile(workerKeyFile)
        }

        val webKeyFile = File("${DirectoryConstants.MASTER_SECURE}/web.key")

        if (!webKeyFile.exists()) {
            val webKey: String = this.generateKey();

            CloudSystemMaster.RUNTIME_VARS.webKey = webKey
            FileUtils.writeStringToFile(webKeyFile, webKey)
        } else {
            CloudSystemMaster.RUNTIME_VARS.webKey = FileUtils.readStringFromFile(webKeyFile)
        }
    }

    private fun generateKey(): String {
        val keyBuilder = StringBuilder()

        (0..10).forEach { _ ->
            keyBuilder.append(UUID.randomUUID().toString().replace("-", ""))
        }

        return keyBuilder.toString()
    }
}