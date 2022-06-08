package systems.beemo.cloudsystem.worker

import io.netty.channel.Channel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.command.CommandManager
import systems.beemo.cloudsystem.library.configuration.ConfigurationLoader
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.worker.commands.HelpCommand
import systems.beemo.cloudsystem.worker.configuration.DefaultCloudConfiguration
import systems.beemo.cloudsystem.worker.configuration.DefaultFolderCreator
import systems.beemo.cloudsystem.worker.configuration.WorkerKeyReader
import systems.beemo.cloudsystem.worker.configuration.models.WorkerConfig
import kotlin.system.exitProcess

class CloudSystemWorker {

    private val logger: Logger = LoggerFactory.getLogger(CloudSystemWorker::class.java)

    companion object {
        lateinit var MASTER_CHANNEL: Channel
        lateinit var KODEIN: DI

        lateinit var WORKER_CONFIG: WorkerConfig

        lateinit var SECRET_KEY: String
        lateinit var WEB_KEY: String
    }

    fun start(args: Array<String>) {
        this.prepareDI()
        this.checkForRoot(args)

        this.executeConfigurations()
        this.startCommandRunner()
    }

    fun shutdownGracefully() {
        this.shutdownCommandRunner()
        this.shutdownThreads()

        logger.info("Thank you for your trust in us. See ya next time!")
    }

    private fun prepareDI() {
        KODEIN = DI {
            bind<CloudSystemWorker>() with singleton { this@CloudSystemWorker }

            bind<ThreadPool>() with singleton { ThreadPool() }

            bind<NettyHelper>() with singleton { NettyHelper() }

            bind<ConfigurationLoader>() with singleton {
                val configurationLoader = ConfigurationLoader()

                configurationLoader.registerConfiguration(DefaultFolderCreator())
                configurationLoader.registerConfiguration(DefaultCloudConfiguration())
                configurationLoader.registerConfiguration(WorkerKeyReader())

                configurationLoader
            }

            bind<CommandManager>() with singleton {
                val commandManager = CommandManager(instance())

                commandManager.registerCommand(HelpCommand(commandManager))

                commandManager
            }

            bind<PacketRegistry>() with singleton {
                val packetRegistry = PacketRegistry()

                packetRegistry
            }
        }
    }

    private fun checkForRoot(args: Array<String>) {
        if (System.getProperty("user.name") == "root" && !args.contains("--enable-root")) {
            logger.error("Please consider not to use the \"root\" user for security reasons!")
            logger.error("If you want to use it anyway, at your own risk, add \"--enable-root\" to the start arguments.")
            exitProcess(0)
        }
    }

    private fun executeConfigurations() {
        val configurationLoader: ConfigurationLoader by KODEIN.instance()
        configurationLoader.executeConfigurations()
    }

    private fun startCommandRunner() {
        val commandManager: CommandManager by KODEIN.instance()
        commandManager.start()
    }

    private fun shutdownCommandRunner() {
        val commandManager: CommandManager by KODEIN.instance()
        commandManager.stop()
    }

    private fun shutdownThreads() {
        val threadPool: ThreadPool by KODEIN.instance()
        threadPool.shutdownPool()
    }
}