package systems.beemo.cloudsystem.master.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import systems.beemo.cloudsystem.library.command.Command
import systems.beemo.cloudsystem.library.command.CommandInformation
import systems.beemo.cloudsystem.library.command.CommandManager

@CommandInformation(
    command = "help",
    description = "This command shows this page",
    aliases = ["h", "?"]
)
class HelpCommand(
    private val commandManager: CommandManager
) : Command {

    private val logger: Logger = LoggerFactory.getLogger(HelpCommand::class.java)

    override fun execute(args: Array<String>): Boolean {
        logger.info("CloudSystem - Command Help Page - Version: 0.0.1")

        commandManager.commands.forEach {
            val commandInformation = it.javaClass.getAnnotation(CommandInformation::class.java)
            logger.info("${commandInformation.command} ${commandInformation.aliases.asList()} | ${commandInformation.description}")
        }

        return true
    }
}