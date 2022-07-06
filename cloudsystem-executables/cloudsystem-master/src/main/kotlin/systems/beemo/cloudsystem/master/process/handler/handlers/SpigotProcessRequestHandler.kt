package systems.beemo.cloudsystem.master.process.handler.handlers

import systems.beemo.cloudsystem.library.dto.WorkerInfo
import systems.beemo.cloudsystem.master.process.handler.ProcessRequestHandler
import systems.beemo.cloudsystem.master.process.models.SpigotProcess
import systems.beemo.cloudsystem.master.process.registry.SpigotProcessRegistry

class SpigotProcessRequestHandler(
    private val spigotProcessRegistry: SpigotProcessRegistry
) : ProcessRequestHandler<SpigotProcess> {

    override fun requestProcessesOnConnect(workerInfo: WorkerInfo) {
        TODO("Not yet implemented")
    }

    override fun requestMultipleProcesses(count: Int, process: SpigotProcess) {
        TODO("Not yet implemented")
    }

    override fun requestProcess(process: SpigotProcess) {
        TODO("Not yet implemented")
    }
}