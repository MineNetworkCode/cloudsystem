package systems.beemo.cloudsystem.master.configuration.models

data class MySqlConfig(
    val databaseHost: String,
    val databaseName: String,
    val playerTableName: String,
    val port: Int,
    val username: String,
    val password: String
)