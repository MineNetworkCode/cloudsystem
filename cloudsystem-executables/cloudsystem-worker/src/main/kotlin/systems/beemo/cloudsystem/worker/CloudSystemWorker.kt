package systems.beemo.cloudsystem.worker

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.protocol.PacketRegistry
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.worker.network.NetworkClientImpl

class CloudSystemWorker {

    companion object {
        lateinit var KODEIN: DI
    }

    fun start(args: Array<String>) {
        this.prepareDI()
        this.startNetworkClient()
    }

    fun shutdownGracefully() {
        this.shutdownThreads()
    }

    private fun prepareDI() {
        KODEIN = DI {
            bind<CloudSystemWorker>() with singleton { this@CloudSystemWorker }

            bind<ThreadPool>() with singleton { ThreadPool() }

            bind<NettyHelper>() with singleton { NettyHelper() }
            bind<PacketRegistry>() with singleton { PacketRegistry() }

            bind<NetworkClientImpl>() with singleton { NetworkClientImpl(instance(), instance()) }
        }
    }

    private fun startNetworkClient() {
        val networkClient: NetworkClientImpl by KODEIN.instance()

        networkClient.startClient("127.0.0.1", 1337)
    }

    private fun shutdownThreads() {
        val threadPool: ThreadPool by KODEIN.instance()
        threadPool.shutdownPool()
    }
}