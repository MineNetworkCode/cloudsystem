package systems.beemo.cloudsystem.master.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.worker.WorkerRegistry

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(NetworkHandler::class.java)

    private val workerRegistry: WorkerRegistry by CloudSystemMaster.KODEIN.instance()

    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet) {
        packet.handle(channelHandlerContext)
    }

    override fun channelInactive(channelHandlerContext: ChannelHandlerContext) {
        val workerInfo = workerRegistry.getWorkerByChannel(channelHandlerContext.channel()) ?: return
        val workerName = "${workerInfo.name}${workerInfo.delimiter}${workerInfo.suffix}"

        workerRegistry.unregisterWorker(workerInfo.uuid)

        logger.info(
            "Removed connection and unregistered Worker:(Name=$workerName, Remote=${
                channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
            })"
        )
    }

    @Throws(Exception::class)
    override fun exceptionCaught(channelHandlerContext: ChannelHandlerContext, cause: Throwable) {
    }
}