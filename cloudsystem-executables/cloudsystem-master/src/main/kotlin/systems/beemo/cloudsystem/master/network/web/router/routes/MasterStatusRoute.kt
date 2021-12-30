package systems.beemo.cloudsystem.master.network.web.router.routes

import com.google.gson.JsonArray
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import io.netty.util.CharsetUtil
import org.kodein.di.instance
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.request.utils.ContentType
import systems.beemo.cloudsystem.library.threading.ThreadPool
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.library.utils.RoundUtils
import systems.beemo.cloudsystem.master.CloudSystemMaster
import systems.beemo.cloudsystem.master.network.web.router.Route
import systems.beemo.cloudsystem.master.worker.WorkerRegistry

class MasterStatusRoute : Route {

    private val workerRegistry: WorkerRegistry by CloudSystemMaster.KODEIN.instance()
    private val threadPool: ThreadPool by CloudSystemMaster.KODEIN.instance()

    override fun handle(channelHandlerContext: ChannelHandlerContext, fullHttpRequest: FullHttpRequest): HttpResponse {
        val statusInfo = Document().appendBoolean("reachable", true)
            .appendLong("startup", HardwareUtils.getSystemStartupTime())
            .appendLong("uptime", HardwareUtils.getSystemUptime())

        val systemInfo = Document().appendDouble("cpuUsage", RoundUtils.roundDouble(HardwareUtils.getCpuUsage(), 2))
            .appendDouble("internalCpuUsage", RoundUtils.roundDouble(HardwareUtils.getInternalCpuUsage(), 2))
            .appendLong("memoryUsage", HardwareUtils.getMemoryUsage() / 1024 / 1024)
            .appendInt("runningThreads", Thread.activeCount())
            .appendInt("runningExecutorServiceThreads", threadPool.internalPool.activeCount)

        val workerInfo = JsonArray()
        workerRegistry.getWorkers().forEach {
            val document = Document().appendString("workerName", "${it.name}${it.delimiter}${it.suffix}")
                .appendLong("memory", it.memory)
                .appendLong("currentMemoryConsumption", it.currentMemoryConsumption / 1024 / 1024)
                .appendDouble("currentCpuConsumption", RoundUtils.roundDouble(it.currentCpuConsumption, 2))

            workerInfo.add(document.getAsJsonObject())
        }

        val responseDocument = Document().appendString("version", "0.0.1")
            .appendDocument("statusInfo", statusInfo)
            .appendDocument("systemInfo", systemInfo)
            .appendJsonElement("workerInfo", workerInfo)

        val httpResponse = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            Unpooled.copiedBuffer("${responseDocument.getAsString()}\r\n", CharsetUtil.UTF_8)
        )

        httpResponse.headers().set("content-type", "application/json; charset=utf-8")
        return httpResponse
    }
}