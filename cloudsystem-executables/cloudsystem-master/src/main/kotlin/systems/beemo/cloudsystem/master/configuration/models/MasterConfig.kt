package systems.beemo.cloudsystem.master.configuration.models

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import systems.beemo.cloudsystem.library.document.Document

data class MasterConfig(
    val cloudServerPort: Int,
    val webServerPort: Int,
    val masterName: String,
    val spigotName: String,
    val spigotVersion: String,
    val bungeeName: String,
    val databaseBackend: String,
    val validWorkers: MutableList<ValidWorkerConfig>,
    val databases: DatabaseConfig
) {

    companion object {
        fun toDocument(masterConfig: MasterConfig): Document {
            return Document().appendInt("cloudServerPort", masterConfig.cloudServerPort)
                .appendInt("webServerPort", masterConfig.webServerPort)
                .appendString("masterName", masterConfig.masterName)
                .appendString("spigotName", masterConfig.spigotName)
                .appendString("spigotVersion", masterConfig.spigotVersion)
                .appendString("bungeeName", masterConfig.bungeeName)
                .appendString("databaseBackend", masterConfig.databaseBackend)
                .appendJsonElement("validWorkers", createValidWorkerConfig(masterConfig))
                .appendDocument("databases", createDatabaseConfig(masterConfig))
        }

        private fun createValidWorkerConfig(masterConfig: MasterConfig): JsonElement {
            val jsonArray = JsonArray()

            masterConfig.validWorkers.forEach {
                val validWorker = Document().appendString("workerName", it.workerName)
                    .appendList("whitelistedIps", it.whitelistedIps)

                jsonArray.add(validWorker.getAsJsonObject())
            }

            return jsonArray
        }

        private fun createDatabaseConfig(masterConfig: MasterConfig): Document {
            val mongoDbConfig = masterConfig.databases.mongoDbConfig
            val faunaConfig = masterConfig.databases.faunaDbConfig

            val mongoConfig = Document().appendString("databaseHost", mongoDbConfig.databaseHost)
                .appendString("databaseName", mongoDbConfig.databaseName)
                .appendString("playerCollectionName", mongoDbConfig.playerCollectionName)
                .appendInt("port", mongoDbConfig.port)
                .appendString("username", mongoDbConfig.username)
                .appendString("password", mongoDbConfig.password)
                .appendBoolean("useAuth", mongoDbConfig.useAuth)

            val faunaDbConfig = Document().appendString("databaseHost", faunaConfig.databaseHost)
                .appendString("databaseName", faunaConfig.databaseName)
                .appendString("secretKey", faunaConfig.secretKey)

            return Document().appendDocument("mongodb", mongoConfig).appendDocument("faunadb", faunaDbConfig)
        }

        fun fromDocument(document: Document): MasterConfig {
            return MasterConfig(
                cloudServerPort = document.getIntValue("cloudServerPort"),
                webServerPort = document.getIntValue("webServerPort"),
                masterName = document.getStringValue("masterName"),
                spigotName = document.getStringValue("spigotName"),
                spigotVersion = document.getStringValue("spigotVersion"),
                bungeeName = document.getStringValue("bungeeName"),
                databaseBackend = document.getStringValue("databaseBackend"),
                validWorkers = processValidWorkers(document.getJsonElementValue("validWorkers").asJsonArray),
                databases = processDatabases(document.getDocument("databases"))
            )
        }

        private fun processValidWorkers(jsonArray: JsonArray): MutableList<ValidWorkerConfig> {
            return jsonArray.map {
                val validWorkerDocument = Document(it.asJsonObject)
                ValidWorkerConfig(
                    workerName = validWorkerDocument.getStringValue("workerName"),
                    whitelistedIps = validWorkerDocument.getList("whitelistedIps") as MutableList<String>
                )
            }.toMutableList()
        }

        private fun processDatabases(document: Document): DatabaseConfig {
            return DatabaseConfig(
                mongoDbConfig = processMongoDbDatabase(Document(document.getJsonElementValue("mongodb").asJsonObject)),
                faunaDbConfig = processFaunaDbDatabase(Document(document.getJsonElementValue("faunadb").asJsonObject))
            )
        }

        private fun processMongoDbDatabase(document: Document): MongoDbConfig {
            return MongoDbConfig(
                databaseHost = document.getStringValue("databaseHost"),
                databaseName = document.getStringValue("databaseName"),
                playerCollectionName = document.getStringValue("playerCollectionName"),
                port = document.getIntValue("port"),
                username = document.getStringValue("username"),
                password = document.getStringValue("password"),
                useAuth = document.getBooleanValue("useAuth")
            )
        }

        private fun processFaunaDbDatabase(document: Document): FaunaDbConfig {
            return FaunaDbConfig(
                databaseHost = document.getStringValue("databaseHost"),
                databaseName = document.getStringValue("databaseName"),
                secretKey = document.getStringValue("secretKey")
            )
        }
    }
}