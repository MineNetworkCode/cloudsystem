package systems.beemo.cloudsystem.worker.tasks

import org.kodein.di.instance
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.network.protocol.outgoing.PacketOutWorkerUpdateLoadStatus
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils
import java.util.*

class UpdateLoadInfoTask : TimerTask() {

    private val networkUtils: NetworkUtils by CloudSystemWorker.KODEIN.instance()

    override fun run() {
        networkUtils.sendPacketAsync(
            PacketOutWorkerUpdateLoadStatus(
                uuid = CloudSystemWorker.WORKER_CONFIG.workerUuid,
                currentOnlineServers = 0, // TODO: GET FROM PROCESS REGISTRY
                currentMemoryConsumption = HardwareUtils.getMemoryUsage(), // TODO: GET FROM PROCESS REGISTRY
                currentCpuConsumption = HardwareUtils.getCpuUsage(),
            ),
            CloudSystemWorker.MASTER_CHANNEL
        )
    }
}