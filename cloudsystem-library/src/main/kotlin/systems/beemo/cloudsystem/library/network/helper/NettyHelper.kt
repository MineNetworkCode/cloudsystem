package systems.beemo.cloudsystem.library.network.helper

import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import io.netty.handler.ssl.util.SelfSignedCertificate
import java.security.cert.CertificateException
import javax.net.ssl.SSLException

class NettyHelper {

    fun getEventLoopGroup(): EventLoopGroup {
        return if (this.isEpoll()) EpollEventLoopGroup() else NioEventLoopGroup()
    }

    fun getClientChannelClass(): Class<out SocketChannel> {
        return if (this.isEpoll()) EpollSocketChannel::class.java else NioSocketChannel::class.java
    }

    fun getServerChannelClass(): Class<out ServerSocketChannel> {
        return if (this.isEpoll()) EpollServerSocketChannel::class.java else NioServerSocketChannel::class.java
    }

    fun createClientCert(): SslContext? {
        var sslContext: SslContext? = null

        try {
            sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: SSLException) {
            e.printStackTrace()
        }

        return sslContext
    }

    fun createServerCert(): SslContext? {
        var sslContext: SslContext? = null

        try {
            val selfSignedCertificate = SelfSignedCertificate()

            sslContext = SslContextBuilder
                .forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey())
                .build()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: SSLException) {
            e.printStackTrace()
        }

        return sslContext
    }

    fun isEpoll(): Boolean {
        return Epoll.isAvailable()
    }
}