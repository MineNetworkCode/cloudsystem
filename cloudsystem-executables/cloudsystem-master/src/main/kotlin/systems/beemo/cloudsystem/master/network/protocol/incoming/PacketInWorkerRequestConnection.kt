package systems.beemo.cloudsystem.master.network.protocol.incoming

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import org.kodein.di.instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.network.protocol.outgoing.PacketOutWorkerConnectionEstablished
import systems.beemo.cloudsystem.master.network.utils.NetworkUtils
import systems.beemo.cloudsystem.master.worker.WorkerRegistry
import kotlin.properties.Delegates

class PacketInWorkerRequestConnection : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInWorkerRequestConnection::class.java)

    private val networkUtils: NetworkUtils by CloudSystemMaster.KODEIN.instance()
    private val workerRegistry: WorkerRegistry by CloudSystemMaster.KODEIN.instance()

    private lateinit var workerInfo: WorkerInfo

    private var verified by Delegates.notNull<Boolean>()

    override fun read(document: Document) {
        val secretKey = document.getStringValue("secretKey")

        verified = secretKey == CloudSystemMaster.SECRET_KEY
        workerInfo = WorkerInfo.fromDocument(document.getDocument("workerInfo"))
    }

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        workerInfo.channel = channelHandlerContext.channel()
        val workerName = "${workerInfo.name}${workerInfo.delimiter}${workerInfo.suffix}"

        if (!this.isWorkerAuthenticated(channelHandlerContext.channel(), workerName)) {
            networkUtils.sendPacketAsync(
                PacketOutWorkerConnectionEstablished(
                    message = "You are not authenticated. Please check your key or the master config!",
                    webKey = "null",
                    successful = false
                ), channelHandlerContext.channel()
            )
            logger.warn(
                "Blocked connection of unauthenticated Worker:(Name=$workerName, Remote=${
                    channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
                })"
            )
            return
        }

        val registered = workerRegistry.registerWorker(workerInfo)

        if (!registered) {
            networkUtils.sendPacketAsync(
                PacketOutWorkerConnectionEstablished(
                    message = "A worker with this uuid already exists!",
                    webKey = "null",
                    successful = false
                ), channelHandlerContext.channel()
            )
            logger.warn(
                "Blocked connection of unauthenticated Worker:(Name=$workerName, Remote=${
                    channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
                })"
            )
            return
        }

        networkUtils.sendPacketAsync(
            PacketOutWorkerConnectionEstablished(
                message = "Connection to master established!",
                webKey = CloudSystemMaster.WEB_KEY,
                successful = true
            ), channelHandlerContext.channel()
        )
        logger.info(
            "New incoming connection of Worker:(Name=$workerName, Remote=${
                channelHandlerContext.channel().remoteAddress().toString().replace("/", "")
            })"
        )

        // TODO: Request processes
    }

    private fun isWorkerAuthenticated(channel: Channel, workerName: String): Boolean {
        if (!verified) return false

        var returnValue = false
        val validWorkers = CloudSystemMaster.MASTER_CONFIG.validWorkers

        validWorkers.forEach {
            val validWorkerName = it.workerName
            val validWorkerWhitelistedIps = it.whitelistedIps

            if (validWorkerName == workerName) {
                val workerIpAddress = channel.remoteAddress().toString().replace("/", "").split(":")[0]

                if (validWorkerWhitelistedIps.contains(workerIpAddress)) {
                    returnValue = true
                }
            }
        }

        return returnValue
    }
}