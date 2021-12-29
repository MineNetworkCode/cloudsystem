package systems.beemo.cloudsystem.library.command

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandInformation(
    val command: String = "Default Command",
    val description: String = "Default Description",
    val aliases: Array<String> = []
)