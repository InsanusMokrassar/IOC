package com.github.insanusmokrassar.IOC.utils

import java.lang.reflect.InvocationTargetException
import java.util.logging.Logger

/**
 * Return new instance of target class
 * @param path Path to package as path.to.package.java
 * @param <T> Target class (or interface)
 * @return New instance of target class
 * @throws ClassExtractException
 */
@Throws(ClassNotFoundException::class, IllegalArgumentException::class)
fun <T> extract(path: String, vararg constructorArgs: Any): T {
    val targetClass = getClass<T>(path)
    val constructors = targetClass.constructors
    constructors.forEach {
        try {
            return if (it.isVarArgs) {
                it.newInstance(constructorArgs) as T
            } else {
                it.newInstance(*constructorArgs) as T
            }
        } catch (e: InstantiationException) {
            Logger.getGlobal().warning("Can't instantiate the instance of class: it may be interface or abstract class")
        } catch (e: IllegalAccessException) {
            Logger.getGlobal().warning("Can't instantiate the instance of class: can't get access for instantiating it")
        } catch (e: InvocationTargetException){
        } catch (e: IllegalArgumentException) {
        }
    }
    throw IllegalArgumentException("Can't create instance of $path with args $constructorArgs")
}

fun <T> getClass(path: String): Class<T> {
    val targetClass = Class.forName(path)
    return targetClass as Class<T>
}