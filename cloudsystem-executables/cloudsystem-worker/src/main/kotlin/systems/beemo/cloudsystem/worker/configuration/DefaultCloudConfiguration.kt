package systems.beemo.cloudsystem.worker.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.configuration.models.WorkerConfig
import java.io.File
import java.util.*

class DefaultCloudConfiguration : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigFile = File("${DirectoryConstants.WORKER_CONFIG}/config.json")

        if (!cloudConfigFile.exists()) {
            val workerDefaultMemory = (HardwareUtils.getSystemMemory() / 3) / 1024 / 1024

            val workerConfig = WorkerConfig(
                cloudServerAddress = "127.0.0.1",
                cloudServerPort = 8000,
                workerName = "Worker-01",
                memory = workerDefaultMemory,
                workerUuid = UUID.randomUUID().toString().replace("-", ""),
                responsibleGroups = mutableListOf()
            )

            WorkerConfig.toDocument(workerConfig).write(cloudConfigFile)
            CloudSystemWorker.WORKER_CONFIG = workerConfig

            logger.warn("It seems the cloud config was just created. Please edit it depending on your wishes!")
        } else {
            CloudSystemWorker.WORKER_CONFIG = WorkerConfig.fromDocument(Document.read(cloudConfigFile))
        }
    }
}