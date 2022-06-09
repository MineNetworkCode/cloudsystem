package systems.beemo.cloudsystem.worker.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.configuration.Configuration
import systems.beemo.cloudsystem.library.document.Document
import systems.beemo.cloudsystem.library.utils.DirectoryConstants
import systems.beemo.cloudsystem.library.utils.HardwareUtils
import systems.beemo.cloudsystem.worker.CloudSystemWorker
import systems.beemo.cloudsystem.worker.configuration.models.WorkerConfig
import java.io.File
import java.util.*

class DefaultCloudConfiguration : Configuration() {

    private val logger: Logger = LoggerFactory.getLogger(DefaultCloudConfiguration::class.java)

    override fun execute() {
        val cloudConfigFile = File("${DirectoryConstants.WORKER_CONFIG}/config.json")

        if (!cloudConfigFile.exists()) {
            val masterAddress = this.readMasterAddress()
            val masterPort = this.readMasterPort()
            val workerName = this.readWorkerName()
            val delimiter = this.readWorkerDelimiter()
            val suffix = this.readWorkerSuffix()
            val memory = this.readWorkerMemory()
            val responsibleGroups = this.readResponsibleGroups()

            val workerConfig = WorkerConfig(
                masterAddress = masterAddress,
                masterPort = masterPort,
                workerName = workerName,
                delimiter = delimiter,
                suffix = suffix,
                memory = memory,
                workerUuid = UUID.randomUUID().toString().replace("-", ""),
                responsibleGroups = responsibleGroups
            )

            WorkerConfig.toDocument(workerConfig).write(cloudConfigFile)
            CloudSystemWorker.RUNTIME_VARS.workerConfig = workerConfig

            logger.warn("It seems the cloud config was just created. Please edit it depending on your wishes!")
        } else {
            CloudSystemWorker.RUNTIME_VARS.workerConfig = WorkerConfig.fromDocument(Document.read(cloudConfigFile))
        }
    }

    private fun readMasterAddress(): String {
        logger.info("Please enter the server address of the master. Default: 127.0.0.1")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return "127.0.0.1"
        }

        return input
    }

    private fun readMasterPort(): Int {
        logger.info("Please enter the port of the master. Default: 8000")
        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return 8000
        }

        return try {
            Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            logger.warn("The port needs to be a non floating number!")
            this.readMasterPort()
        }
    }

    private fun readWorkerName(): String {
        logger.info("Please enter a name for the worker. Default: Worker")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return "Worker"
        }

        return input
    }

    private fun readWorkerDelimiter(): String {
        logger.info("Please enter a delimiter for the worker name. Default: -")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return "-"
        }

        return input
    }

    private fun readWorkerSuffix(): String {
        logger.info("Please enter a suffix for the worker name. Default: 01")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return "01"
        }

        return input
    }

    private fun readWorkerMemory(): Long {
        logger.info("Please enter the max memory for this worker. Default: ${(HardwareUtils.getSystemMemory() / 3) / 1024 / 1024}")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return (HardwareUtils.getSystemMemory() / 3) / 1024 / 1024
        }

        return try {
            input.toLong()
        } catch (e: NumberFormatException) {
            this.readWorkerMemory()
        }
    }

    private fun readResponsibleGroups(): MutableList<String> {
        logger.info("Please enter the responsible groups for the worker. Split them by \",\" Default: All groups")

        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return mutableListOf()
        }

        val groups = input.split("[ ,]+".toRegex())

        logger.info("You choose " + groups.toMutableList() + ". Is that right? (Y/n)")

        if (this.yesOrNo()) {
            return groups.toMutableList()
        }

        return this.readResponsibleGroups()
    }

    private fun yesOrNo(): Boolean {
        val input = bufferedReader.readLine()

        if (input.equals("")) {
            return true;
        }

        if (!input.equals("Y", true) && !input.equals("N", true)) {
            logger.warn("Please enter only \"Y\" for yes and \"N\" for no!")
            return this.yesOrNo()
        }

        return input.equals("Y", true)
    }
}