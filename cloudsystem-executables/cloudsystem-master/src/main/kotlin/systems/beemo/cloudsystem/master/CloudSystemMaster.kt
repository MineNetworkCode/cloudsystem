package systems.beemo.cloudsystem.master

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.command.CommandManager
import systems.beemo.cloudsystem.library.configuration.ConfigurationLoader
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketId
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.master.commands.HelpCommand
import systems.beemo.cloudsystem.master.configuration.*
import systems.beemo.cloudsystem.master.configuration.models.MasterConfig
import systems.beemo.cloudsystem.master.groups.bungee.BungeeGroupHandler
import systems.beemo.cloudsystem.master.groups.spigot.SpigotGroupHandler
import systems.beemo.cloudsystem.master.network.NetworkServerImpl
import systems.beemo.cloudsystem.master.network.protocol.incoming.PacketInWorkerRequestConnection
import systems.beemo.cloudsystem.master.network.protocol.incoming.PacketInWorkerUpdateLoadStatus
import systems.beemo.cloudsystem.master.network.protocol.outgoing.process.PacketOutRequestProcess
import systems.beemo.cloudsystem.master.network.protocol.outgoing.worker.PacketOutWorkerConnectionEstablished
import systems.beemo.cloudsystem.master.network.utils.NetworkUtils
import systems.beemo.cloudsystem.master.network.web.CloudWebServerImpl
import systems.beemo.cloudsystem.master.network.web.router.Router
import systems.beemo.cloudsystem.master.network.web.router.routes.MasterStatusRoute
import systems.beemo.cloudsystem.master.process.ProcessRegistry
import systems.beemo.cloudsystem.master.process.ProcessRequestHandler
import systems.beemo.cloudsystem.master.worker.WorkerRegistry
import kotlin.system.exitProcess

class CloudSystemMaster {

    private val logger: Logger = LoggerFactory.getLogger(CloudSystemMaster::class.java)

    companion object {
        lateinit var KODEIN: DI
        lateinit var MASTER_CONFIG: MasterConfig
        lateinit var SECRET_KEY: String
        lateinit var WEB_KEY: String
    }

    fun start(args: Array<String>) {
        this.prepareDI()
        this.checkForRoot(args)

        this.executeConfigurations()
        this.startCommandRunner()
        this.startNetworkServer()
        this.startWebServer()
    }

    fun shutdownGracefully() {
        this.shutdownCommandRunner()
        this.shutdownNetworkServer()
        this.shutdownWebServer()
        this.shutdownThreads()

        logger.info("Thank you for your trust in us. See ya next time!")
    }

    private fun prepareDI() {
        KODEIN = DI {
            bind<CloudSystemMaster>() with singleton { this@CloudSystemMaster }

            bind<ThreadPool>() with singleton { ThreadPool() }

            bind<NettyHelper>() with singleton { NettyHelper() }
            bind<NetworkUtils>() with singleton { NetworkUtils() }

            bind<SpigotGroupHandler>() with singleton { SpigotGroupHandler() }
            bind<BungeeGroupHandler>() with singleton { BungeeGroupHandler() }

            bind<ConfigurationLoader>() with singleton {
                val configurationLoader = ConfigurationLoader()

                configurationLoader.registerConfiguration(DefaultFolderCreator())
                configurationLoader.registerConfiguration(DefaultCloudConfiguration())
                configurationLoader.registerConfiguration(SpigotDownloadConfiguration())
                configurationLoader.registerConfiguration(BungeeDownloadConfiguration())
                configurationLoader.registerConfiguration(WorkerKeyCreator())
                configurationLoader.registerConfiguration(WebKeyCreator())
                configurationLoader.registerConfiguration(SpigotGroupLoader(instance()))
                configurationLoader.registerConfiguration(BungeeGroupLoader(instance()))

                configurationLoader
            }

            bind<CommandManager>() with singleton {
                val commandManager = CommandManager(instance())

                commandManager.registerCommand(HelpCommand(commandManager))

                commandManager
            }

            bind<WorkerRegistry>() with singleton { WorkerRegistry() }
            bind<PacketRegistry>() with singleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerIncomingPacket(PacketId.PACKET_REQUEST_CONNECTION, PacketInWorkerRequestConnection::class.java)
                packetRegistry.registerIncomingPacket(PacketId.PACKET_UPDATE_LOAD_STATUS, PacketInWorkerUpdateLoadStatus::class.java)
                packetRegistry.registerOutgoingPacket(PacketId.PACKET_ESTABLISHED_CONNECTION, PacketOutWorkerConnectionEstablished::class.java)
                packetRegistry.registerOutgoingPacket(PacketId.PACKET_REQUEST_PROCESS, PacketOutRequestProcess::class.java)

                packetRegistry
            }

            bind<ProcessRegistry>() with singleton { ProcessRegistry() }
            bind<ProcessRequestHandler>() with singleton { ProcessRequestHandler(instance(), instance(), instance(), instance(), instance()) }

            bind<Router>() with singleton {
                val router = Router()

                router.registerRoute("/status", MasterStatusRoute())

                router
            }

            bind<NetworkServerImpl>() with singleton { NetworkServerImpl(instance(), instance()) }
            bind<CloudWebServerImpl>() with singleton { CloudWebServerImpl(instance()) }
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

    private fun startNetworkServer() {
        val networkServer: NetworkServerImpl by KODEIN.instance()
        networkServer.startServer(MASTER_CONFIG.cloudServerPort)
    }

    private fun startWebServer() {
        val cloudWebServer: CloudWebServerImpl by KODEIN.instance()
        cloudWebServer.startServer(MASTER_CONFIG.webServerPort)
    }

    private fun shutdownCommandRunner() {
        val commandManager: CommandManager by KODEIN.instance()
        commandManager.stop()
    }

    private fun shutdownWebServer() {
        val cloudWebServer: CloudWebServerImpl by KODEIN.instance()
        cloudWebServer.shutdownGracefully()
    }

    private fun shutdownNetworkServer() {
        val networkServer: NetworkServerImpl by KODEIN.instance()
        networkServer.shutdownGracefully()
    }

    private fun shutdownThreads() {
        val threadPool: ThreadPool by KODEIN.instance()
        threadPool.shutdownPool()
    }
}