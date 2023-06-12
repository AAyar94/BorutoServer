package com.aayar94.plugins

import com.aayar94.di.koinModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(koinModule)
    }
}
