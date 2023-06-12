package com.aayar94.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.root() {
    get("/") {
        call.respond(
            message = "Welcome to Baruto API!",
            status = HttpStatusCode.OK
        )
    }
}