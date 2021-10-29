package systems.beemo.cloudsystem.library.cache

import java.util.stream.Collectors

class Cache<K, T> {

    private val cacheMap: MutableMap<K, T> = mutableMapOf()

    fun put(key: K, value: T) {
        synchronized(cacheMap) {
            if (cacheMap.containsKey(key) || cacheMap.containsValue(value)) {
                this.cacheMap.replace(key, value)
                return
            }

            this.cacheMap[key] = value
        }
    }

    operator fun set(key: K, value: T) {
        synchronized(cacheMap) {
            this.cacheMap[key] = value
        }
    }

    operator fun get(key: K): T? {
        synchronized(cacheMap) {
            return cacheMap[key]
        }
    }

    fun getOrDefault(key: K, value: T?): T? {
        synchronized(cacheMap) {
            return cacheMap[key] ?: return value
        }
    }

    fun remove(key: K) {
        synchronized(cacheMap) {
            cacheMap.remove(key)
        }
    }

    fun replace(key: K, value: T) {
        synchronized(cacheMap) {
            if (!cacheMap.containsKey(key) || !cacheMap.containsValue(value)) {
                cacheMap[key] = value
                return
            }

            cacheMap.replace(key, value)
        }
    }

    fun containsKey(key: K): Boolean {
        synchronized(cacheMap) {
            return cacheMap.containsKey(key)
        }
    }

    fun containsValue(value: T): Boolean {
        synchronized(cacheMap) {
            return cacheMap.containsValue(value)
        }
    }

    fun getCacheValues(): MutableList<T> {
        synchronized(cacheMap) {
            return cacheMap.values.stream().collect(Collectors.toList())
        }
    }

    fun size(): Int {
        synchronized(cacheMap) {
            return cacheMap.size
        }
    }

    fun clear() {
        synchronized(cacheMap) {
            cacheMap.clear()
        }
    }
}