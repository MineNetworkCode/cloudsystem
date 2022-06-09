package systems.beemo.cloudsystem.library.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StringUtils {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StringUtils::class.java)

        fun printHeader(service: String) {
            println(
                "           ____    ____   _                    ______  __                         __  \n" +
                    "          |_   \\  /   _| (_)                 .' ___  |[  |                       |  ] \n" +
                    "            |   \\/   |   __   _ .--.  .---. / .'   \\_| | |  .--.   __   _    .--.| |  \n" +
                    "            | |\\  /| |  [  | [ `.-. |/ /__\\\\| |        | |/ .'`\\ \\[  | | | / /'`\\' |  \n" +
                    "           _| |_\\/_| |_  | |  | | | || \\__.,\\ `.___.'\\ | || \\__. | | \\_/ |,| \\__/  |  \n" +
                    "          |_____||_____|[___][___||__]'.__.' `.____ .'[___]'.__.'  '.__.'_/ '.__.;__] \n"
            )
            logger.info("Service: $service | JVM: v${System.getProperty("java.version")} | User: ${System.getProperty("user.name")}")
        }
    }
}