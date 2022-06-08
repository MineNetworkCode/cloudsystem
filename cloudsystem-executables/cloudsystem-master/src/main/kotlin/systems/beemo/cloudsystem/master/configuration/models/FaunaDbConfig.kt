package systems.beemo.cloudsystem.master.configuration.models

data class FaunaDbConfig(
    val databaseHost: String,
    val databaseName: String,
    val secretKey: String,
)