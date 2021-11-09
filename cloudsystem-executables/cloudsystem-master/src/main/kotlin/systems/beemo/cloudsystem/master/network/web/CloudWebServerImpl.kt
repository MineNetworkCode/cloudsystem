package systems.beemo.cloudsystem.master.network.web

import io.netty.channel.Channel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import systems.beemo.cloudsystem.library.network.helper.NettyHelper
import systems.beemo.cloudsystem.library.network.web.CloudWebServer

class CloudWebServerImpl(
    nettyHelper: NettyHelper
) : CloudWebServer(nettyHelper) {

    override fun preparePipeline(channel: Channel) {
        channel.pipeline()
            .addLast(HttpRequestDecoder(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, false))
            .addLast(HttpObjectAggregator(Integer.MAX_VALUE))
            .addLast(HttpResponseEncoder())
            .addLast(CloudWebHandler())
    }
}