package com.github.insanusmokrassar.IOC.core

interface IOCStrategy {
    /**
     * Using by IOC for getting instance from strategy
     * @param args Args for using in strategy
     * @return new, cached or other instance of object
     * @throws ResolveStrategyException Throw when instance can't be resolved
     */
    @Throws(ResolveStrategyException::class)
    fun <T: Any> getInstance(vararg args: Any?): T
}