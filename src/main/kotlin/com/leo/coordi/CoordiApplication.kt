package com.leo.coordi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching


@SpringBootApplication
@EnableCaching
class CoordiApplication

fun main(args: Array<String>) {
    runApplication<CoordiApplication>(*args)
}