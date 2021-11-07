package systems.beemo.cloudsystem.master.network.protocol.incoming

import io.netty.channel.ChannelHandlerContext
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.master.CloudSystemMaster
import kotlin.properties.Delegates

class PacketInWorkerRequestConnection : Packet {

    lateinit var workerInfo: WorkerInfo

    var verified by Delegates.notNull<Boolean>()

    override fun read(document: Document) {
        val secretKey = document.getStringValue("secretKey")

        if (secretKey == CloudSystemMaster.SECRET_KEY) {
            workerInfo = WorkerInfo.fromDocument(document.getDocument("workerInfo"))
        } else verified = false
    }

    override fun handle(channelHandlerContext: ChannelHandlerContext) {

    }
}