package systems.beemo.cloudsystem.worker.tasks

import org.kodein.di.instance
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.network.protocol.out.PacketOutWorkerUpdateLoadStatus
import systems.beemo.cloudsystem.worker.network.utils.NetworkUtils
import java.util.TimerTask

class UpdateLoadInfoTask : TimerTask() {

    private val networkUtils: NetworkUtils by CloudSystemWorker.KODEIN.instance()

    override fun run() {
        networkUtils.sendPacket(
            PacketOutWorkerUpdateLoadStatus(
                uuid = CloudSystemWorker.RUNTIME_VARS.workerConfig.workerUuid,
                currentOnlineServers = 0, // TODO: GET FROM PROCESS REGISTRY
                currentMemoryConsumption = HardwareUtils.getMemoryUsage(), // TODO: GET FROM PROCESS REGISTRY
                currentCpuConsumption = HardwareUtils.getCpuUsage(),
            ),
            CloudSystemWorker.RUNTIME_VARS.masterChannel
        )
    }
}