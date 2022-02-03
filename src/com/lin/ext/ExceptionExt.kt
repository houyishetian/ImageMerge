package com.lin.ext

fun <T> tryCatchAllExceptions(block: () -> T, exceptionValue: T? = null): T? {
    return try {
        block.invoke()
    } catch (e: Exception) {
        exceptionValue
    }
}