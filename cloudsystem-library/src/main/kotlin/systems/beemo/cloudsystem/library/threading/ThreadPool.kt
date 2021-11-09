package systems.beemo.cloudsystem.library.threading

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ThreadPool {

    val internalPool: ThreadPoolExecutor = Executors.newFixedThreadPool(15) {
        val thread = Executors.defaultThreadFactory().newThread(it)
        thread.isDaemon = true
        thread.name = "cloudsystem-${thread.id}"
        thread
    } as ThreadPoolExecutor

    fun shutdownPool() {
        internalPool.shutdown()

        try {
            if (!internalPool.awaitTermination(1000, TimeUnit.MILLISECONDS)) internalPool.shutdownNow()
        } catch (e: InterruptedException) {
            internalPool.shutdownNow()
        }
    }
}