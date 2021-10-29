package systems.beemo.cloudsystem.library.utils

import systems.beemo.cloudsystem.library.request.DefaultRequestFactory
import java.net.SocketTimeoutException

class DownloadUtils {

    companion object {
        private val requestProperty: HashMap<String, String> =
            hashMapOf("User-Agent" to "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")

        @Throws(SocketTimeoutException::class)
        fun downloadFile(url: String, to: String, connectTimeout: Int, readTimeout: Int, useCache: Boolean) {
            val inputStream = DefaultRequestFactory()
                .newFactory(url)
                .setRequestProperty("User-Agent", requestProperty.getOrDefault("User-Agent", ":^)"))
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .setUseCache(useCache)
                .fire()

            FileUtils.copyFileFromStream(inputStream, to)
        }
    }
}