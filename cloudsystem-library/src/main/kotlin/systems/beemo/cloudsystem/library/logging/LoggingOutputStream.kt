package systems.beemo.cloudsystem.library.logging

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.logging.Level

class LoggingOutputStream(
    private val cloudLogger: CloudLogger,
    private val level: Level
) : ByteArrayOutputStream() {

    override fun flush() {
        val content = this.toString(StandardCharsets.UTF_8)
        this.reset()

        if (content.isNotEmpty() && !content.equals(cloudLogger.lineSeparator) && !content.contains("SLF4J"))
            cloudLogger.logp(this.level, "", "", content)
    }
}