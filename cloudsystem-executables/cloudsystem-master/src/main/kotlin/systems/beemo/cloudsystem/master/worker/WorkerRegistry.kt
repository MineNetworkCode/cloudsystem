package systems.beemo.cloudsystem.master.worker

import io.netty.channel.Channel
import systems.beemo.cloudsystem.library.dto.WorkerInfo
import java.util.stream.Collectors

class WorkerRegistry {

    private val workers: MutableMap<String, WorkerInfo> = mutableMapOf()

    fun registerWorker(workerInfo: WorkerInfo): Boolean {
        if (this.workers.containsKey(workerInfo.uuid)) return false
        this.workers[workerInfo.uuid] = workerInfo

        return this.workers.containsKey(workerInfo.uuid)
    }

    fun unregisterWorker(uuid: String) {
        this.workers.remove(uuid)
    }

    fun updateWorker(uuid: String, workerInfo: WorkerInfo) {
        this.unregisterWorker(uuid)
        this.registerWorker(workerInfo)
    }

    fun getWorker(uuid: String): WorkerInfo? {
        return this.workers[uuid]
    }

    fun getWorkersByGroup(group: String): MutableList<WorkerInfo> {
        return this.getWorkers().stream()
            .filter { it.responsibleGroups.contains(group) }
            .collect(Collectors.toList())
    }

    fun getWorkerByChannel(channel: Channel): WorkerInfo? {
        return this.getWorkers().stream()
            .filter { it.channel == channel }
            .findFirst()
            .orElse(null)
    }

    fun getWorkers(): MutableList<WorkerInfo> {
        return this.workers.values.stream().collect(Collectors.toList())
    }

    fun getLeastUsedWorker(group: String): WorkerInfo? {
        val workersForGroup = this.getWorkersByGroup(group)

        if (workersForGroup.isEmpty()) return null

        var bestWorker: WorkerInfo? = null

        for (worker in workersForGroup) {
            if (bestWorker == null) {
                bestWorker = worker
                continue
            }

            if (worker.currentMemoryConsumption < bestWorker.currentMemoryConsumption
                && worker.currentCpuConsumption < bestWorker.currentCpuConsumption
            ) {
                bestWorker = worker
            }
        }

        if (bestWorker == workersForGroup.first()) {
            for (worker in workersForGroup) {
                if (bestWorker == null) {
                    bestWorker = worker
                    continue
                }

                if (worker.currentMemoryConsumption < bestWorker.currentMemoryConsumption) {
                    bestWorker = worker
                }
            }
        }

        return bestWorker
    }
}