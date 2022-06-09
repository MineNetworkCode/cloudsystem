package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.configuration.models.DatabaseConfig
import systems.beemo.cloudsystem.master.configuration.models.MasterConfig
import systems.beemo.cloudsystem.master.configuration.models.MongoDbConfig
import systems.beemo.cloudsystem.master.configuration.models.ValidWorkerConfig
import java.io.File

class DefaultCloudConfiguration : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    private val availableDatabaseBackends: MutableSet<String> = mutableSetOf(
        "FILE",
        "MONGO"
    )


    override fun execute() {
        val cloudConfigFile = File("${DirectoryConstants.MASTER_CONFIG_CLOUD}/config.json")

        if (!cloudConfigFile.exists()) {
            val masterPort = this.readPort("master", 8000)
            val webServerPort = this.readPort("web server", 8080)
            val databaseBackend = this.readDatabaseBackend()

            val masterConfig = MasterConfig(
                masterPort = masterPort,
                webServerPort = webServerPort,
                databaseBackend = "FILE",
                validWorkers = this.createValidWorkerConfig(),
                databases = this.createDatabaseConfig()
            )

            MasterConfig.toDocument(masterConfig).write(cloudConfigFile)
            CloudSystemMaster.RUNTIME_VARS.masterConfig = masterConfig

            logger.warn("It seems the cloud config was just created. Please edit it depending on your wishes!")
        } else {
            CloudSystemMaster.RUNTIME_VARS.masterConfig = MasterConfig.fromDocument(Document.read(cloudConfigFile))
        }
    }

    private fun readPort(service: String, defaultPort: Int): Int {
        logger.info("Please pick a port for the $service to run on. Default: $defaultPort")
        val input = bufferedReader.readLine()

        try {
            val port = Integer.parseInt(input)

            if (!HardwareUtils.isPortFree(port)) {
                logger.warn("Your entered port is already in use!")
                return this.readPort(service, defaultPort)
            }

            return port
        } catch (e: NumberFormatException) {
            logger.warn("The port needs to be a non floating number!")
            return this.readPort(service, defaultPort)
        }
    }

    private fun readDatabaseBackend(): String {
        logger.info("Please choose a database backend. $availableDatabaseBackends")
        val input = bufferedReader.readLine()

        if (!(availableDatabaseBackends.contains(input.uppercase()))) {
            return this.readDatabaseBackend()
        }

        return input.uppercase()
    }

    private fun createValidWorkerConfig(): MutableList<ValidWorkerConfig> {
        return mutableListOf(
            ValidWorkerConfig(
                workerName = "Worker-01",
                whitelistedIps = mutableListOf("127.0.0.1")
            )
        )
    }

    private fun createDatabaseConfig(): DatabaseConfig {
        val mongoDbConfig = MongoDbConfig(
            databaseHost = "localhost",
            databaseName = "cloudsystem",
            playerCollectionName = "playerCollection",
            port = 27017,
            username = "<change me>",
            password = "<change me>",
            useAuth = false
        )

        return DatabaseConfig(mongoDbConfig)
    }
}