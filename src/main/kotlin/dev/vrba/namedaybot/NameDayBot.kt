package dev.vrba.namedaybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class NameDayBot

fun main(args: Array<String>) {
    runApplication<NameDayBot>(*args)
}
