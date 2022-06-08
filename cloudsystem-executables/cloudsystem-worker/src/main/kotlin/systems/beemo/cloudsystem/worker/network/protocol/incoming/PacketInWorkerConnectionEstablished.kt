package systems.beemo.cloudsystem.worker.network.protocol.incoming

import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import kotlin.properties.Delegates

class PacketInWorkerConnectionEstablished : Packet {

    private val logger: Logger = LoggerFactory.getLogger(PacketInWorkerConnectionEstablished::class.java)

    private lateinit var message: String
    private lateinit var webKey: String

    private var successful by Delegates.notNull<Boolean>()

    override fun read(document: Document) {
        message = document.getStringValue("message")
        webKey = document.getStringValue("webKey")
        successful = document.getBooleanValue("successful")
    }

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        if (successful) {
            CloudSystemWorker.WEB_KEY = webKey
            logger.info(message)
        } else logger.error(message)
    }
}