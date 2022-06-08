package systems.beemo.cloudsystem.master.process

import systems.beemo.cloudsystem.library.cache.Cache
import systems.beemo.cloudsystem.library.process.ProcessType
import systems.beemo.cloudsystem.master.process.models.CloudProcess
import java.util.stream.Collectors

class ProcessRegistry {

    private val cloudProcesses: Cache<String, CloudProcess> = Cache()
    private val portsInUse: MutableList<Int> = mutableListOf()

    fun registerProcess(cloudProcess: CloudProcess) {
        if (cloudProcesses.containsKey(cloudProcess.uuid)) return

        this.cloudProcesses[cloudProcess.uuid] = cloudProcess
    }

    fun unregisterProcess(cloudProcess: CloudProcess) {
        this.cloudProcesses.remove(cloudProcess.uuid)
    }

    fun updateProcess(cloudProcess: CloudProcess) {
        this.unregisterProcess(cloudProcess)
        this.registerProcess(cloudProcess)
    }

    fun getProcess(uuid: String): CloudProcess? {
        return this.cloudProcesses[uuid]
    }

    fun getRunningProcessCount(name: String): Int {
        return this.cloudProcesses.getCacheValues().stream().filter { it.name.startsWith(name) }.collect(Collectors.toList()).size
    }

    fun getSpigotProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        val returnMap: MutableMap<String, Pair<String, Int>> = mutableMapOf()

        for (cloudProcess in cloudProcesses.getCacheValues()) {
            if (cloudProcess.type == ProcessType.BUNGEE) continue
            if (cloudProcess.lobbyServer) continue

            val pair = Pair(cloudProcess.ip, cloudProcess.port)
            returnMap[cloudProcess.name] = pair
        }

        return returnMap
    }

    fun getLobbyProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        val returnMap: MutableMap<String, Pair<String, Int>> = mutableMapOf()

        for (cloudProcess in cloudProcesses.getCacheValues()) {
            if (cloudProcess.type == ProcessType.BUNGEE) continue
            if (!cloudProcess.lobbyServer) continue

            val pair = Pair(cloudProcess.ip, cloudProcess.port)
            returnMap[cloudProcess.name] = pair
        }

        return returnMap
    }

    fun registerPort(port: Int) {
        this.portsInUse.add(port)
    }

    fun unregisterPort(port: Int) {
        this.portsInUse.remove(port)
    }

    fun isPortInUse(port: Int): Boolean {
        return this.portsInUse.contains(port)
    }
}