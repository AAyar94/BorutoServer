package com.aayar94.plugins

import com.aayar94.routes.getAllHeroes
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import com.aayar94.routes.root
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()

        static("/images") {
            resources("images")
        }
    }
}
