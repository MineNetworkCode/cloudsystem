package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.master.CloudSystemMaster
import java.io.File
import java.util.*

class WorkerKeyCreator : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(WorkerKeyCreator::class.java)

    override fun execute() {
        val workerKeyFile = File("${DirectoryConstants.MASTER_SECURE}/worker.key")

        if(!workerKeyFile.exists()) {
            val workerKey = StringBuilder()

            (0..10).forEach { _ ->
                workerKey.append(UUID.randomUUID().toString().replace("-", ""))
            }

            CloudSystemMaster.SECRET_KEY = workerKey.toString()
            FileUtils.writeStringToFile(workerKeyFile, workerKey.toString())

            logger.warn("It seems the worker key was just created. Please copy it to the \"${DirectoryConstants.WORKER_SECURE}\" folder!")
        } else {
            CloudSystemMaster.SECRET_KEY = FileUtils.readStringFromFile(workerKeyFile)
        }
    }
}