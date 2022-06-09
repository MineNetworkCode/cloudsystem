package systems.beemo.cloudsystem.worker.runtime

import io.netty.channel.Channel
import systems.beemo.cloudsystem.worker.configuration.models.WorkerConfig

class RuntimeVars {

    lateinit var masterChannel: Channel
    lateinit var workerConfig: WorkerConfig
    lateinit var secretKey: String
    lateinit var webKey: String
}