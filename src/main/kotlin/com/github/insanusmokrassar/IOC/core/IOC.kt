package com.github.insanusmokrassar.IOC.core

import com.github.insanusmokrassar.IOC.utils.extract
import com.github.insanusmokrassar.iobjectk.exceptions.ReadException
import com.github.insanusmokrassar.iobjectk.interfaces.IObject

val strategiesKey = "strategies"
val nameKey = "name"
val packageKey = "package"
val configKey = "config"

/**
 * <pre>
 *     {
 *          "strategies": [
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
fun loadConfig(config: IObject<Any>, into: IOC = IOC()) {
    val strategiesList = config.get<List<IObject<Any>>>(strategiesKey)
    strategiesList.forEach {
        val args: Array<Any>
        if (it.keys().contains(configKey)) {
            args = try {
                it.get<List<Any>>(configKey).toTypedArray()
            } catch (e: ReadException) {
                arrayOf(it.get<Any>(configKey))
            }
        } else {
            args = arrayOf()
        }
        into.register(
                it.get(nameKey),
                extract(
                        it.get(packageKey),
                        *args
                )
        )
    }
}

class IOC {
    private val strategies = HashMap<String, IOCStrategy>()
    private val subscribers = ArrayList<(String) -> Unit>()

    fun register(key: String, strategy: IOCStrategy) {
        synchronized(strategies) {
            strategies.put(key, strategy)
        }
        onChanged("Strategy for key \"$key\" with strategy \"$strategy\" registered.\n")
    }

    @Throws(ResolveStrategyException::class)
    fun <T: Any> resolve(key: String, vararg args: Any): T {
        try {
            try {
                return strategies[key]!!.getInstance<T>(*args)
            } catch (e: NullPointerException) {
                return extract(key, *args)
            }
        } catch (e: Throwable) {
            throw ResolveStrategyException("Can't resolve strategy for some of reason ($key, $args)", e)
        }
    }

    private fun onChanged(what: String) {
        subscribers.forEach {
            it(what)
        }
    }
}