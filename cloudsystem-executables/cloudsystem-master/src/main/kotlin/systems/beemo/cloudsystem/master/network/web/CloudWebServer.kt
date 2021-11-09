package systems.beemo.cloudsystem.master.network.web

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.master.CloudSystemMaster
import java.security.cert.CertificateException
import javax.net.ssl.SSLException
import kotlin.system.exitProcess

class CloudWebServer {

    private val logger: Logger = LoggerFactory.getLogger(CloudWebServer::class.java)

    private lateinit var workerGroup: EventLoopGroup

    fun startServer() {
        val epoll = Epoll.isAvailable()

        workerGroup = if (epoll) EpollEventLoopGroup() else NioEventLoopGroup()

        val rightChannelClass = if (epoll) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java

        Thread({
            try {
                ServerBootstrap().group(workerGroup)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.IP_TOS, 24)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.AUTO_READ, true)

                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.AUTO_READ, true)

                    .channel(rightChannelClass)
                    .childHandler(object : ChannelInitializer<Channel>() {
                        override fun initChannel(channel: Channel) {

                            channel.pipeline()
                                .addLast(HttpRequestDecoder(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false))
                                .addLast(HttpObjectAggregator(Integer.MAX_VALUE))
                                .addLast(HttpResponseEncoder())
                                .addLast(CloudWebHandler())
                        }
                    }).bind(CloudSystemMaster.MASTER_CONFIG.webServerPort).addListener {
                        if (it.isSuccess) {
                            logger.info("Web Server started and was bound to 127.0.0.1:${CloudSystemMaster.MASTER_CONFIG.webServerPort}")
                        } else {
                            logger.error("Something went wrong while starting the web server")
                            exitProcess(0)
                        }
                    }
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                    .channel().closeFuture().syncUninterruptibly()
            } catch (e: Exception) {
                logger.error(e.message)
            } finally {
                workerGroup.shutdownGracefully()
            }
        }, "cloudsystem-webserver").start()
    }

    fun shutdownGracefully() {
        workerGroup.shutdownGracefully()
    }
}