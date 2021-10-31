package systems.beemo.cloudsystem.worker.bootstrap

import systems.beemo.cloudsystem.worker.CloudSystemWorker

fun main(args: Array<String>) {
    Thread.currentThread().name = "cloudsystem-${Thread.currentThread().id}"

    val cloudSystemWorker = CloudSystemWorker()
    cloudSystemWorker.start(args)

    Runtime.getRuntime().addShutdownHook(Thread({
        cloudSystemWorker.shutdownGracefully()
    }, "cloudsystem-shutdown"))
}