package systems.beemo.cloudsystem.library.logging.formatter

import systems.beemo.cloudsystem.library.logging.color.ConsoleColors
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord

class LoggingFormatter : Formatter() {

    private val simpleDateFormat = SimpleDateFormat("HH:mm:ss.SSS")
    private val user = System.getProperty("user.name", "user")

    override fun format(logRecord: LogRecord): String {
        var formattedMessage = "${ConsoleColors.WHITE}[" +
            "${ConsoleColors.GREEN}${simpleDateFormat.format(logRecord.millis)}" +
            "${ConsoleColors.WHITE} | " +
            "${ConsoleColors.YELLOW}$user" +
            "${ConsoleColors.WHITE}] " +
            "${this.getLevelColor(logRecord.level)}${logRecord.level.name} " +
            "${ConsoleColors.WHITE}(" +
            "${ConsoleColors.PURPLE}${Thread.currentThread().name}" +
            "${ConsoleColors.WHITE})" +
            "${ConsoleColors.WHITE}: " +
            "${if (logRecord.level === Level.SEVERE || logRecord.level === Level.WARNING) this.getLevelColor(logRecord.level) else ConsoleColors.WHITE}${
                this.formatMessage(logRecord)
            }" +
            "\n"

        if (logRecord.thrown != null) {
            val stringWriter = StringWriter()
            logRecord.thrown.printStackTrace(PrintWriter(stringWriter))

            formattedMessage += stringWriter
        }

        return formattedMessage
    }

    private fun getLevelColor(level: Level): String {
        return when (level.name) {
            "INFO" -> ConsoleColors.GREEN
            "WARNING" -> ConsoleColors.YELLOW
            "SEVERE" -> ConsoleColors.RED
            else -> ConsoleColors.WHITE
        }
    }
}