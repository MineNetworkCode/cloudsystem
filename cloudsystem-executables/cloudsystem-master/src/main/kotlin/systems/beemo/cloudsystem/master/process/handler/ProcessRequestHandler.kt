package systems.beemo.cloudsystem.master.process.handler

import systems.beemo.cloudsystem.library.dto.WorkerInfo

interface ProcessRequestHandler<T> {

    fun requestProcessesOnConnect(workerInfo: WorkerInfo)
    fun requestProcess(process: T)
    fun requestMultipleProcesses(count: Int, process: T)
}