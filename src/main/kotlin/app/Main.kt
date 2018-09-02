package app

import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create().apply {
        sessionHandler(::fileSessionHandler)
    }.start(7000)
}
