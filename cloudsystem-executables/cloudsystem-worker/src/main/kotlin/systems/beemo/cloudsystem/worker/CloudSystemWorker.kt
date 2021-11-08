package systems.beemo.cloudsystem.worker

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.ConfigurationLoader
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketId
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.worker.configuration.DefaultCloudConfiguration
import systems.beemo.cloudsystem.worker.configuration.DefaultFolderCreator
import systems.beemo.cloudsystem.worker.configuration.models.WorkerConfig
import systems.beemo.cloudsystem.worker.network.NetworkClientImpl
import systems.beemo.cloudsystem.worker.network.protocol.incoming.PacketInWorkerConnectionEstablished
import systems.beemo.cloudsystem.worker.network.protocol.outgoing.PacketOutWorkerRequestConnection
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils
import kotlin.system.exitProcess

class CloudSystemWorker {

    private val logger: Logger = LoggerFactory.getLogger(CloudSystemWorker::class.java)

    companion object {
        lateinit var KODEIN: DI
        lateinit var WORKER_CONFIG: WorkerConfig
        lateinit var WEB_KEY: String
    }

    fun start(args: Array<String>) {
        this.prepareDI()
        this.checkForRoot(args)

        this.executeConfigurations()
        this.startNetworkClient()
    }

    fun shutdownGracefully() {
        this.shutdownThreads()

        logger.info("Thank you for your trust in us. See ya next time!")
    }

    private fun prepareDI() {
        KODEIN = DI {
            bind<CloudSystemWorker>() with singleton { this@CloudSystemWorker }

            bind<ThreadPool>() with singleton { ThreadPool() }

            bind<NettyHelper>() with singleton { NettyHelper() }
            bind<NetworkUtils>() with singleton { NetworkUtils() }

            bind<ConfigurationLoader>() with singleton {
                val configurationLoader = ConfigurationLoader()

                configurationLoader.registerConfiguration(DefaultFolderCreator())
                configurationLoader.registerConfiguration(DefaultCloudConfiguration())

                configurationLoader
            }

            bind<PacketRegistry>() with singleton {
                val packetRegistry = PacketRegistry()

                packetRegistry.registerOutgoingPacket(PacketId.PACKET_REQUEST_CONNECTION, PacketOutWorkerRequestConnection::class.java)
                packetRegistry.registerIncomingPacket(PacketId.PACKET_ESTABLISHED_CONNECTION, PacketInWorkerConnectionEstablished::class.java)

                packetRegistry
            }

            bind<NetworkClientImpl>() with singleton { NetworkClientImpl(instance(), instance()) }
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

    private fun startNetworkClient() {
        val networkClient: NetworkClientImpl by KODEIN.instance()

        networkClient.startClient(WORKER_CONFIG.cloudServerAddress, WORKER_CONFIG.cloudServerPort)
    }

    private fun shutdownThreads() {
        val threadPool: ThreadPool by KODEIN.instance()
        threadPool.shutdownPool()
    }
}