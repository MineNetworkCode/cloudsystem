package systems.beemo.cloudsystem.library.threading

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ThreadPool {

    val threadPool: ExecutorService = Executors.newFixedThreadPool(15) {
        val thread = Executors.defaultThreadFactory().newThread(it)
        thread.isDaemon = true
        thread.name = "cloudsystem-${thread.id}"
        thread
    }

    fun shutdownPool() {
        threadPool.shutdown()

        try {
            if (!threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS)) threadPool.shutdownNow()
        } catch (e: InterruptedException) {
            threadPool.shutdownNow()
        }
    }
}