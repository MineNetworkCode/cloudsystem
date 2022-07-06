package systems.beemo.cloudsystem.master.process.registry

import systems.beemo.cloudsystem.library.cache.Cache
import systems.beemo.cloudsystem.master.process.models.AbstractProcess
import java.util.stream.Collectors

open class ProcessRegistry<T : AbstractProcess> {

    val processes: Cache<String, T> = Cache()

    fun registerProcess(process: T) {
        if (processes.containsKey(process.uuid)) return

        this.processes[process.uuid] = process
    }

    fun unregisterProcess(process: T) {
        this.processes.remove(process.uuid)
    }

    fun updateProcess(process: T) {
        this.unregisterProcess(process)
        this.registerProcess(process)
    }

    fun getProcess(uuid: String): T? {
        return this.processes[uuid]
    }

    fun getRunningProcessCount(name: String): Int {
        return this.processes.getCacheValues().stream().filter { it.name.startsWith(name) }.collect(Collectors.toList()).size
    }
}