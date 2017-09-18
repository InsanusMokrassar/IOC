package com.github.insanusmokrassar.IOC.core

import com.github.insanusmokrassar.IOC.utils.extract
import com.github.insanusmokrassar.IObjectK.exceptions.ReadException
import com.github.insanusmokrassar.IObjectK.interfaces.IObject

val strategiesKey = "strategies"
val nameKey = "name"
val packageKey = "package"
val configKey = "config"

private val iocMap = HashMap<String, IOC>()

fun getOrCreateIOC(name: String): IOC {
    synchronized(iocMap, {
        if (!iocMap.containsKey(name)) {
            iocMap.put(name, IOC())
        }
        return iocMap[name]!!
    })
}

/**
 * <pre>
 *     {
 *          "IOC NAME": [
 *              {
 *                  "name": "NameOfStrategy",
 *                  "package": "class.path.to.class.of.strategy",
 *                  "config": any //optional, can be a list (will used as vararg of params), object or static value
 *              }
 *          ]
 *     }
 * </pre>
 */
@Throws(IllegalArgumentException::class)
fun loadConfig(config: IObject<Any>) {
    config.keys().forEach {
        val ioc = getOrCreateIOC(it)
        val strategiesList = config.get<List<IObject<Any>>>(it)
        strategiesList.forEach {
            val config = getConfig(it)
            ioc.register(
                    it.get(nameKey),
                    extract(
                            it.get(packageKey),
                            *config
                    )
            )
        }
    }
}

fun getConfig(from: IObject<Any>) : Array<Any> {
    return try {
        from.get<List<Any>>(configKey).toTypedArray()
    } catch (e: ClassCastException) {
        arrayOf(from.get(configKey))
    } catch (e: ReadException) {
        emptyArray()
    }
}

class IOC internal constructor(){
    private val strategies = HashMap<String, IOCStrategy>()

    fun register(key: String, strategy: IOCStrategy) {
        synchronized(strategies) {
            strategies.put(key, strategy)
        }
    }

    @Throws(ResolveStrategyException::class)
    fun <T: Any> resolve(key: String, vararg args: Any?): T {
        return try {
            try {
                strategies[key]!!.getInstance(*args)
            } catch (e: NullPointerException) {
                extract(key, *args)
            }
        } catch (e: Throwable) {
            throw ResolveStrategyException("Can't resolve strategy for some of reason ($key, $args)", e)
        }
    }
}