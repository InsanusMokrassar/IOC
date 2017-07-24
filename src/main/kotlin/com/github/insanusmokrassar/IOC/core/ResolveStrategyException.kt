package com.github.insanusmokrassar.IOC.core

class ResolveStrategyException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}