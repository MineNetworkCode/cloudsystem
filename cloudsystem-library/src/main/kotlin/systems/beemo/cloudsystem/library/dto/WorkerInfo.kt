package systems.beemo.cloudsystem.library.dto

import io.netty.channel.Channel
import systems.beemo.cloudsystem.library.document.Document

data class WorkerInfo(
    val uuid: String,
    val name: String,
    val delimiter: String,
    val suffix: String,
    val memory: Long,
    val currentMemoryConsumption: Long,
    val currentCpuConsumption: Double,
    val responsibleGroups: MutableList<String>,
    var channel: Channel? = null // Might be null because we don't need it in the packets but in the master
) {
    companion object {
        fun fromDocument(document: Document): WorkerInfo {
            return WorkerInfo(
                document.getStringValue("uuid"),
                document.getStringValue("name"),
                document.getStringValue("delimiter"),
                document.getStringValue("suffix"),
                document.getLongValue("memory"),
                document.getLongValue("currentMemoryConsumption"),
                document.getDoubleValue("currentCpuConsumption"),
                document.getList("responsibleGroups") as MutableList<String>
            )
        }

        fun toDocument(workerInfo: WorkerInfo): Document {
            return Document().appendString("uuid", workerInfo.uuid)
                .appendString("name", workerInfo.name)
                .appendString("delimiter", workerInfo.delimiter)
                .appendString("suffix", workerInfo.suffix)
                .appendLong("memory", workerInfo.memory)
                .appendLong("currentMemoryConsumption", workerInfo.currentMemoryConsumption)
                .appendDouble("currentCpuConsumption", workerInfo.currentCpuConsumption)
                .appendList("responsibleGroups", workerInfo.responsibleGroups)
        }
    }
}