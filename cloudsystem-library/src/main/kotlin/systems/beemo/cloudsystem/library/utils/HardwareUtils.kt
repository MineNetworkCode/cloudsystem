package systems.beemo.cloudsystem.library.utils

import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.net.ServerSocket

class HardwareUtils {

    companion object {
        fun getCpuUsage(): Double {
            return this.getOperatingSystemBean().cpuLoad * 100
        }

        fun getInternalCpuUsage(): Double {
            return this.getOperatingSystemBean().processCpuLoad * 100
        }

        fun getMemoryUsage(): Long {
            return this.getSystemMemory() - this.getOperatingSystemBean().freeMemorySize
        }

        fun getSystemMemory(): Long {
            return this.getOperatingSystemBean().totalMemorySize
        }

        fun getSystemStartupTime(): Long {
            return this.getRuntimeBean().startTime
        }

        fun getSystemUptime(): Long {
            return System.currentTimeMillis() - this.getRuntimeBean().uptime
        }

        fun getRuntimeBean(): RuntimeMXBean {
            return ManagementFactory.getRuntimeMXBean()
        }

        fun getOperatingSystemBean(): OperatingSystemMXBean {
            return ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        }

        fun isPortFree(port: Int): Boolean {
            return try {
                val serverSocket = ServerSocket(port)
                serverSocket.close()

                true
            } catch (e: Exception) {
                false
            }
        }
    }
}