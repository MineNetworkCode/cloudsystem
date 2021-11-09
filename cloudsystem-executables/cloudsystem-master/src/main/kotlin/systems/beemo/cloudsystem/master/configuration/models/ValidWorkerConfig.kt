package systems.beemo.cloudsystem.master.configuration.models

data class ValidWorkerConfig(
    val workerName: String,
    val whitelistedIps: MutableList<String>
)