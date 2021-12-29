package systems.beemo.cloudsystem.library.command

interface Command {

    fun execute(args: Array<String>): Boolean
}