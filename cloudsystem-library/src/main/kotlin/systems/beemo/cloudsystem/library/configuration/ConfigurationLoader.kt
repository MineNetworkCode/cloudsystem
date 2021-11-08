package systems.beemo.cloudsystem.library.configuration

class ConfigurationLoader {

    private val configurations: MutableList<Configuration> = mutableListOf()

    fun registerConfiguration(configuration: Configuration) {
        this.configurations.add(configuration)
    }

    fun executeConfigurations() {
        this.configurations.forEach { it.execute() }
    }
}