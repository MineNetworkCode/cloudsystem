package systems.beemo.cloudsystem.worker.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.FileUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import java.io.File
import kotlin.system.exitProcess

class WorkerKeyReader : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(WorkerKeyReader::class.java)

    override fun execute() {
        val workerKeyFile = File("${DirectoryConstants.WORKER_SECURE}/worker.key")

        if (!workerKeyFile.exists()) {
            logger.error("Can't find the worker key in \"${DirectoryConstants.WORKER_SECURE}\"! Did you copy it?")
            exitProcess(0)
        } else {
            CloudSystemWorker.RUNTIME_VARS.secretKey = FileUtils.readStringFromFile(workerKeyFile)
        }
    }
}