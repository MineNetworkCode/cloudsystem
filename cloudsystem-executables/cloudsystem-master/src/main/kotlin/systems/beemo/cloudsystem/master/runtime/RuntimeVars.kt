package systems.beemo.cloudsystem.master.runtime

import systems.beemo.cloudsystem.master.configuration.models.MasterConfig
import kotlin.properties.Delegates

class RuntimeVars {

    lateinit var masterConfig: MasterConfig
    lateinit var secretKey: String
    lateinit var webKey: String

    var debug by Delegates.notNull<Boolean>()
}