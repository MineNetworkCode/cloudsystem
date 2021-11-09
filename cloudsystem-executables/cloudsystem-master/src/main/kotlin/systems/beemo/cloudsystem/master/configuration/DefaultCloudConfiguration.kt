package systems.beemo.cloudsystem.master.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.configuration.models.*
import java.io.File

class DefaultCloudConfiguration : Configuration {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigFile = File("${DirectoryConstants.MASTER_CONFIG_CLOUD}/config.json")

        if (!cloudConfigFile.exists()) {
            val masterConfig = MasterConfig(
                cloudServerPort = 8000,
                webServerPort = 8080,
                masterName = "Master",
                spigotName = "SPIGOT",
                spigotVersion = "1.8.8",
                bungeeName = "BUNGEECORD",
                databaseBackend = "FILE",
                validWorkers = this.createValidWorkerConfig(),
                databases = this.createDatabaseConfig()
            )

            MasterConfig.toDocument(masterConfig).write(cloudConfigFile)
            CloudSystemMaster.MASTER_CONFIG = masterConfig

            logger.warn("It seems the cloud config was just created. Please edit it depending on your wishes!")
        } else {
            CloudSystemMaster.MASTER_CONFIG = MasterConfig.fromDocument(Document.read(cloudConfigFile))
        }
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

        val mySqlConfig = MySqlConfig(
            databaseHost = "localhost",
            databaseName = "cloudsystem",
            playerTableName = "playerTable",
            port = 3306,
            username = "<change me>",
            password = "<change me>"
        )

        return DatabaseConfig(mongoDbConfig, mySqlConfig)
    }
}