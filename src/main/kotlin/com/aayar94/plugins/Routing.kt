package com.aayar94.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import com.aayar94.routes.root

fun Application.configureRouting() {
    routing {
        root()
    }
}
