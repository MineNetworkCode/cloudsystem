package systems.beemo.cloudsystem.library.network.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.handler.ssl.SslContext
import systems.beemo.cloudsystem.library.network.helper.NettyHelper

abstract class NetworkClient(
    private val nettyHelper: NettyHelper
) {

    private lateinit var workerGroup: EventLoopGroup

    fun startClient(host: String, port: Int) {
        workerGroup = nettyHelper.getEventLoopGroup()

        val sslContext = nettyHelper.createClientCert()
        val channelClass = nettyHelper.getClientChannelClass()

        Thread({
            try {
                Bootstrap().group(workerGroup)
                    .option(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.IP_TOS, 24)
                    .option(ChannelOption.TCP_NODELAY, true)

                    .channel(channelClass)
                    .handler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {
                            preparePipeline(sslContext, channel)
                        }
                    }).connect(host, port).channel().closeFuture().addListener {
                        println("Network timeout! Reconnecting in 5 seconds...")
                        Thread.sleep(5000)

                        this.startClient(host, port)
                    }.syncUninterruptibly()
            } catch (e: Exception) {
                println(e)
            } finally {
                workerGroup.shutdownGracefully()
            }
        }, "cloudsystem-client").start()
    }

    abstract fun preparePipeline(sslContext: SslContext?, channel: Channel)
}