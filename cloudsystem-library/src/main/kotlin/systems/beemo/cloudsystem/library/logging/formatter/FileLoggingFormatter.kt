package systems.beemo.cloudsystem.library.logging.formatter

import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.logging.Formatter
import java.util.logging.LogRecord

class FileLoggingFormatter : Formatter() {

    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS")

    override fun format(logRecord: LogRecord): String {
        var formattedMessage =
            "[${simpleDateFormat.format(logRecord.millis)}] ${logRecord.level.localizedName}: ${this.formatMessage(logRecord)}\n"

        if (logRecord.thrown != null) {
            val stringWriter = StringWriter()
            logRecord.thrown.printStackTrace(PrintWriter(stringWriter))

            formattedMessage += stringWriter
        }

        return formattedMessage
    }
}