package com.aayar94.routes

import com.aayar94.repository.HeroRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHeroes() {
    val heroRepository: HeroRepository by inject()

    get("/boruto/heroes/search") {
        val name = call.request.queryParameters["name"]

        val apiResponse = heroRepository.searchHeroes(name = name)
        call.respond(
            message = apiResponse,
            status = HttpStatusCode.OK
        )
    }
}