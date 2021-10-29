package systems.beemo.cloudsystem.library.logging

import java.io.IOException
import java.util.logging.Handler
import java.util.logging.LogRecord

class LoggingHandler(
    private val cloudLogger: CloudLogger
) : Handler() {

    override fun publish(logRecord: LogRecord) {
        if (this.isLoggable(logRecord)) this.handle(this.cloudLogger.formatter.format(logRecord))
    }

    override fun flush() {}

    override fun close() {}

    private fun handle(message: String) {
        try {
            val consoleReader = this.cloudLogger.consoleReader
            consoleReader.print('\r' + message)
            consoleReader.drawLine()
            consoleReader.flush()
        } catch (e: IOException) {
        }
    }
}