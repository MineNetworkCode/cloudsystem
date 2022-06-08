package systems.beemo.cloudsystem.master.configuration.models

data class DatabaseConfig(
    val mongoDbConfig: MongoDbConfig,
    val faunaDbConfig: FaunaDbConfig
)