package systems.beemo.cloudsystem.library.network.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.handler.ssl.SslContext
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import java.util.function.Consumer

abstract class NetworkServer(
    private val nettyHelper: NettyHelper
) {

    private lateinit var bossGroup: EventLoopGroup
    private lateinit var workerGroup: EventLoopGroup

    fun startServer(port: Int, callback: Consumer<Boolean>) {
        bossGroup = nettyHelper.getEventLoopGroup()
        workerGroup = nettyHelper.getEventLoopGroup()

        val sslContext = nettyHelper.createServerCert()
        val channelClass = nettyHelper.getServerChannelClass()

        Thread({
            try {
                ServerBootstrap().group(bossGroup, workerGroup)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.IP_TOS, 24)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.AUTO_READ, true)

                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)

                    .channel(channelClass)
                    .childHandler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {
                            preparePipeline(sslContext, channel)
                        }
                    }).bind(port)
                    .addListener { callback.accept(it.isSuccess) }
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    .channel().closeFuture().syncUninterruptibly()
            } catch (e: Exception) {
                callback.accept(false)
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        }, "cloudsystem-server").start()
    }

    fun shutdownGracefully() {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }

    abstract fun preparePipeline(sslContext: SslContext?, channel: Channel)
}