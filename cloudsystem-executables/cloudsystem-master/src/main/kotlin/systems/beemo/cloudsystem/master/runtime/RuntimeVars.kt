package systems.beemo.cloudsystem.master.runtime

import systems.beemo.cloudsystem.master.configuration.models.MasterConfig

class RuntimeVars {

    lateinit var masterConfig: MasterConfig
    lateinit var secretKey: String
    lateinit var webKey: String
}