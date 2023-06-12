package com.aayar94.di

import com.aayar94.repository.HeroRepository
import com.aayar94.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}