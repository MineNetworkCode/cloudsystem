package systems.beemo.cloudsystem.worker.configuration.models

import systems.beemo.cloudsystem.library.document.Document

class WorkerConfig(
    val masterAddress: String,
    val masterPort: Int,
    val workerName: String,
    val delimiter: String,
    val suffix: String,
    val memory: Long,
    val workerUuid: String,
    val responsibleGroups: MutableList<String>
) {

    companion object {
        fun toDocument(workerConfig: WorkerConfig): Document {
            return Document().appendString("masterAddress", workerConfig.masterAddress)
                .appendInt("masterPort", workerConfig.masterPort)
                .appendString("workerName", workerConfig.workerName)
                .appendString("delimiter", workerConfig.delimiter)
                .appendString("suffix", workerConfig.suffix)
                .appendLong("memory", workerConfig.memory)
                .appendString("workerUuid", workerConfig.workerUuid)
                .appendList("responsibleGroups", workerConfig.responsibleGroups)
        }

        fun fromDocument(document: Document): WorkerConfig {
            return WorkerConfig(
                masterAddress = document.getStringValue("masterAddress"),
                masterPort = document.getIntValue("masterPort"),
                workerName = document.getStringValue("workerName"),
                delimiter = document.getStringValue("delimiter"),
                suffix = document.getStringValue("suffix"),
                memory = document.getLongValue("memory"),
                workerUuid = document.getStringValue("workerUuid"),
                responsibleGroups = document.getList("responsibleGroups") as MutableList<String>
            )
        }
    }
}