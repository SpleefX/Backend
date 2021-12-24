package net.spleefx.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpleefX

fun main(args: Array<String>) {
    runApplication<SpleefX>(*args)
}
