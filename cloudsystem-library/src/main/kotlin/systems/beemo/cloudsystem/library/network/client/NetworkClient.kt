package systems.beemo.cloudsystem.library.network.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.handler.ssl.SslContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.network.helper.NettyHelper

abstract class NetworkClient(
    private val nettyHelper: NettyHelper
) {

    private val logger: Logger = LoggerFactory.getLogger(NetworkClient::class.java)

    private lateinit var workerGroup: EventLoopGroup

    fun startClient(host: String, port: Int) {
        workerGroup = nettyHelper.getEventLoopGroup("cloud-client")

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
                        Thread.sleep(5000)
                        logger.warn("Network timeout! Reconnecting in 5 seconds...")

                        this.startClient(host, port)
                    }.syncUninterruptibly()
            } catch (e: Exception) {
                logger.error(e.message)
            } finally {
                workerGroup.shutdownGracefully()
            }
        }, "cloudsystem-client").start()
    }

    fun shutdownGracefully() {
        workerGroup.shutdownGracefully()
    }

    abstract fun preparePipeline(sslContext: SslContext?, channel: Channel)
}