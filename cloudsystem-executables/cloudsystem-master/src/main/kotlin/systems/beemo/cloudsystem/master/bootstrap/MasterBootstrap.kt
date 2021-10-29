package systems.beemo.cloudsystem.master.bootstrap

import systems.beemo.cloudsystem.master.CloudSystemMaster

fun main(args: Array<String>) {
    Thread.currentThread().name = "cloudsystem-${Thread.currentThread().id}"

    val cloudSystemMaster = CloudSystemMaster()
    cloudSystemMaster.start(args)

    Runtime.getRuntime().addShutdownHook(Thread({
        cloudSystemMaster.shutdownGracefully()
    }, "cloudsystem-shutdown"))
}