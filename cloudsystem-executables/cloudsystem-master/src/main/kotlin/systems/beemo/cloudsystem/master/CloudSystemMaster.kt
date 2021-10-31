package systems.beemo.cloudsystem.master

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.master.network.NetworkServerImpl
import kotlin.system.exitProcess

class CloudSystemMaster {

    private val logger: Logger = LoggerFactory.getLogger(CloudSystemMaster::class.java)

    companion object {
        lateinit var KODEIN: DI
    }

    fun start(args: Array<String>) {
        this.prepareDI()
        this.checkForRoot(args)

        this.startNetworkServer()
    }

    fun shutdownGracefully() {
        this.shutdownThreads()
    }

    private fun prepareDI() {
        KODEIN = DI {
            bind<CloudSystemMaster>() with singleton { this@CloudSystemMaster }

            bind<ThreadPool>() with singleton { ThreadPool() }

            bind<NettyHelper>() with singleton { NettyHelper() }
            bind<PacketRegistry>() with singleton { PacketRegistry() }

            bind<NetworkServerImpl>() with singleton { NetworkServerImpl(instance(), instance()) }
        }
    }

    private fun checkForRoot(args: Array<String>) {
        if (System.getProperty("user.name") == "root" && !args.contains("--enable-root")) {
            logger.error("Please consider not to use the \"root\" user for security reasons!")
            logger.error("If you want to use it anyway, at your own risk, add \"--enable-root\" to the start arguments.")
            exitProcess(0)
        }
    }

    private fun startNetworkServer() {
        val networkServer: NetworkServerImpl by KODEIN.instance()

        networkServer.startServer(1337) {
            if (it) logger.info("Network Server started and was bound to 127.0.0.1:1337")
            else {
                logger.error("Something went wrong while starting the server")
                exitProcess(0)
            }
        }
    }

    private fun shutdownThreads() {
        val threadPool: ThreadPool by KODEIN.instance()
        threadPool.shutdownPool()
    }
}