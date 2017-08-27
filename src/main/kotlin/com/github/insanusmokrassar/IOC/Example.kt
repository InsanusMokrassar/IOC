package com.github.insanusmokrassar.IOC

import com.github.insanusmokrassar.IOC.core.IOC
import com.github.insanusmokrassar.IOC.core.IOCStrategy
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

fun main(args: Array<String>) {
    args.forEach {
        println(it)
    }
    val ioc = IOC()

    ioc.register("random", RandomOutputStreamStrategy)

    val stream = ioc.resolve<OutputStream>("random", "system", "file")
    stream.write('H'.toInt())
    stream.flush()
    stream.close()
}

object RandomOutputStreamStrategy: IOCStrategy {
    override fun <T: Any> getInstance(vararg args: Any): T {
        val streams = ArrayList<OutputStream>()
        args.forEach {
            if (it is String) {
                when (it) {
                    "system" -> streams.add(System.out)
                    "file" -> streams.add(FileOutputStream("output.txt"))
                }
            }
        }
        return streams[Random().nextInt(streams.size)] as T
    }
}
