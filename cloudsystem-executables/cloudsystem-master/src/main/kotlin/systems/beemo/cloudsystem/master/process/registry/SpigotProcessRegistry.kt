package systems.beemo.cloudsystem.master.process.registry

import systems.beemo.cloudsystem.master.process.models.SpigotProcess

class SpigotProcessRegistry : ProcessRegistry<SpigotProcess>() {

    fun getSpigotProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        val returnMap: MutableMap<String, Pair<String, Int>> = mutableMapOf()

        for (cloudProcess in processes.getCacheValues()) {
            if (cloudProcess.lobbyServer) continue

            val pair = Pair(cloudProcess.ip, cloudProcess.port)
            returnMap[cloudProcess.name] = pair
        }

        return returnMap
    }

    fun getLobbyProcessesToRegister(): MutableMap<String, Pair<String, Int>> {
        val returnMap: MutableMap<String, Pair<String, Int>> = mutableMapOf()

        for (cloudProcess in processes.getCacheValues()) {
            if (!cloudProcess.lobbyServer) continue

            val pair = Pair(cloudProcess.ip, cloudProcess.port)
            returnMap[cloudProcess.name] = pair
        }

        return returnMap
    }
}