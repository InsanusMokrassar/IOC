package com.github.insanusmokrassar.IOC.core.strategies

import com.github.insanusmokrassar.IOC.core.IOCStrategy
import com.github.insanusmokrassar.IOC.utils.extract
import java.util.*
import java.util.logging.Logger


class CacheIOCStrategy(private val classPath: String) : IOCStrategy {
    private val instances: MutableMap<String, Any> = HashMap()

    override fun <T: Any> getInstance(vararg args: Any): T {
        try {
            val name = args[0] as String
            if (instances.containsKey(name)) {
                return instances.get(name) as T
            }
            val resultObject = extract<T>(classPath, Arrays.copyOfRange(args, 1, args.size))
            instances.put(name, resultObject)
            return resultObject
        } catch (e: IllegalArgumentException) {
            Logger.getGlobal().warning(e.message)
            throw IllegalArgumentException("Can't resolve dependency with args $args")
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Arguments is empty")
        }
    }
}