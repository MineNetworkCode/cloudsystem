package systems.beemo.cloudsystem.worker.configuration.models

import systems.beemo.cloudsystem.library.document.Document

class WorkerConfig(
    val cloudServerAddress: String,
    val cloudServerPort: Int,
    val workerName: String,
    val memory: Long,
    val workerUuid: String,
    val responsibleGroups: MutableList<String>
) {

    companion object {
        fun toDocument(workerConfig: WorkerConfig): Document {
            return Document().appendString("cloudServerAddress", workerConfig.cloudServerAddress)
                .appendInt("cloudServerPort", workerConfig.cloudServerPort)
                .appendString("workerName", workerConfig.workerName)
                .appendLong("memory", workerConfig.memory)
                .appendString("workerUuid", workerConfig.workerUuid)
                .appendList("responsibleGroups", workerConfig.responsibleGroups)
        }

        fun fromDocument(document: Document): WorkerConfig {
            return WorkerConfig(
                cloudServerAddress = document.getStringValue("cloudServerAddress"),
                cloudServerPort = document.getIntValue("cloudServerPort"),
                workerName = document.getStringValue("workerName"),
                memory = document.getLongValue("memory"),
                workerUuid = document.getStringValue("workerUuid"),
                responsibleGroups = document.getList("responsibleGroups") as MutableList<String>
            )
        }
    }
}