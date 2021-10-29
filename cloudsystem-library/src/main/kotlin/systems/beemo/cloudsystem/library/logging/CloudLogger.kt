package systems.beemo.cloudsystem.library.logging

import jline.console.ConsoleReader
import org.fusesource.jansi.AnsiConsole
import systems.beemo.cloudsystem.library.logging.formatter.FileLoggingFormatter
import systems.beemo.cloudsystem.library.logging.formatter.LoggingFormatter
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.Logger

class CloudLogger(
    private val logsFile: File
) : Logger("CloudSystem-Logging", null) {

    val lineSeparator: String = System.lineSeparator()

    var consoleReader: ConsoleReader = ConsoleReader(System.`in`, System.out)
    var formatter: Formatter = LoggingFormatter()

    init {
        try {
            this.init()
        } catch (e: IOException) {
            getLogger(this::class.java.name).log(Level.SEVERE, null, e)
        }
    }

    @Throws(IOException::class)
    private fun init() {
        this.level = Level.ALL
        this.consoleReader.expandEvents = false

        if (!logsFile.exists()) logsFile.mkdirs()

        val fileHandler = FileHandler("${logsFile.canonicalPath}/latest.log", 1024000, 8, true)
        fileHandler.formatter = FileLoggingFormatter()
        this.addHandler(fileHandler)

        val loggingHandler = LoggingHandler(this)
        loggingHandler.formatter = this.formatter
        loggingHandler.level = Level.INFO
        this.addHandler(loggingHandler)

        System.setOut(PrintStream(LoggingOutputStream(this, Level.INFO), true))
        System.setErr(PrintStream(LoggingOutputStream(this, Level.SEVERE), true))
    }

    fun shutdown() {
        AnsiConsole.systemUninstall()
        (File(logsFile, "latest.log.0.lck")).delete()
    }
}