package systems.beemo.cloudsystem.worker.network.protocol.incoming.process

import io.netty.channel.ChannelHandlerContext
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.network.protocol.Packet
import systems.beemo.cloudsystem.library.process.ProcessStage
import systems.beemo.cloudsystem.library.process.ProcessType
import kotlin.properties.Delegates

class PacketInRequestProcess : Packet {

    lateinit var groupName: String
    lateinit var name: String
    lateinit var uuid: String
    lateinit var ip: String
    lateinit var type: ProcessType
    lateinit var stage: ProcessStage

    var maxMemory by Delegates.notNull<Int>()
    var minMemory by Delegates.notNull<Int>()
    var port by Delegates.notNull<Int>()
    var maxPlayers by Delegates.notNull<Int>()
    var joinPower by Delegates.notNull<Int>()
    var maintenance by Delegates.notNull<Boolean>()

    var lobbyServer by Delegates.notNull<Boolean>()
    var dynamicServer by Delegates.notNull<Boolean>()
    var staticServer by Delegates.notNull<Boolean>()

    override fun read(document: Document) {
        groupName = document.getStringValue("groupName")
        name = document.getStringValue("name")
        uuid = document.getStringValue("uuid")
        ip = document.getStringValue("ip")
    }

    override fun handle(channelHandlerContext: ChannelHandlerContext) {
        // TODO: Continue
    }
}