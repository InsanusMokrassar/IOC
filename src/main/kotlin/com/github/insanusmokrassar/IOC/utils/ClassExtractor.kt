package com.github.insanusmokrassar.IOC.utils

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

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
    val constructors = targetClass.getConstructors()
    constructors.forEach {
        if (it.parameterTypes.size == constructorArgs.size) {
            try {
                return it.newInstance(*constructorArgs) as T
            } catch (e: InstantiationException) {
                throw IllegalArgumentException("Can't instantiate the instance of class: it may be interface or abstract class", e);
            } catch (e: IllegalAccessException) {
                throw IllegalArgumentException("Can't instantiate the instance of class: can't get access for instantiating it", e);
            } catch (e: InvocationTargetException){
            } catch (e: IllegalArgumentException) {
            }

        }
    }
    throw IllegalArgumentException("Can't find constructor for this args");
}

fun <T> getClass(path: String): Class<T> {
    val targetClass = Class.forName(path)
    return targetClass as Class<T>
}