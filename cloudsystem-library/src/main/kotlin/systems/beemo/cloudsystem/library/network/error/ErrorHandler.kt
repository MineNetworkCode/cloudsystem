package systems.beemo.cloudsystem.library.network.error

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.dto.WorkerInfo

class ErrorHandler {

    private val logger: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)

    fun handleError(workerInfo: WorkerInfo?, cause: Throwable) {
        if (workerInfo == null) {
            this.printMessage(cause)
            this.publishSentry(cause)
            this.publishDatabase(cause)
        } else {
            this.printMessage(workerInfo, cause)
            this.publishSentry(workerInfo, cause)
            this.publishDatabase(workerInfo, cause)
        }
    }

    private fun printMessage(cause: Throwable) {
        logger.error("An internal error occurred but no worker seems to be affected!")
        logger.error(cause.message)
    }

    private fun printMessage(workerInfo: WorkerInfo, cause: Throwable) {
        val workerName = "${workerInfo.name}${workerInfo.delimiter}${workerInfo.suffix}"

        logger.error("An internal error occurred and $workerName is affected of it!")
        logger.error(cause.message)
    }

    private fun publishSentry(cause: Throwable) {
        // TODO: Publish sentry
    }

    private fun publishSentry(workerInfo: WorkerInfo, cause: Throwable) {
        // TODO: Publish sentry
    }

    private fun publishDatabase(cause: Throwable) {
        // TODO: Publish database
    }

    private fun publishDatabase(workerInfo: WorkerInfo, cause: Throwable) {
        // TODO: Publish database
    }
}