/*
 * This file is part of spleefx-backend, licensed under the MIT License.
 *
 *  Copyright (c) Revxrsal <reflxction.github@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.spleefx.web

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.spleefx.web.debug.DebugRedisAccessor
import net.spleefx.web.docs.DocumentCache
import net.spleefx.web.docs.WikiFileWatcher
import net.spleefx.web.util.JsonConfig
import net.spleefx.web.util.scheduleIncrementingIdSaving
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import java.io.File
import java.nio.file.Files
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@SpringBootApplication
class SpleefX {

    @Bean
    fun servletContainer(): ServletWebServerFactory {
        val tomcat: TomcatServletWebServerFactory = object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: org.apache.catalina.Context) {
                val securityConstraint = SecurityConstraint()
                securityConstraint.userConstraint = "CONFIDENTIAL"
                val collection = SecurityCollection()
                collection.addPattern("/*")
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }
        tomcat.addAdditionalTomcatConnectors(httpConnector)
        return tomcat
    }

    private val httpConnector: Connector
        get() {
            val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
            connector.scheme = "http"
            connector.port = 8080
            connector.secure = false
            connector.redirectPort = 443
            return connector
        }
}

fun main(args: Array<String>) {
    DocumentCache.loadAll()
    scheduleIncrementingIdSaving()
    DebugRedisAccessor.schedule()
    WikiFileWatcher.watch()
    runApplication<SpleefX>(*args)
}

val DIR = File("/root/spleefx-web/data").also { Files.createDirectories(it.toPath()) }
val PASTE = File(DIR, "Pastes").also { Files.createDirectories(it.toPath()) }
val WIKI = File(DIR, "Wiki")
val CONFIG = JsonConfig("config.json")
val EXECUTOR: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
val MAPPER = ObjectMapper()

inline fun scheduleAsync(delay: Long, interval: Long, unit: TimeUnit, crossinline task: () -> Unit) {
    EXECUTOR.scheduleAtFixedRate({
        try {
            task()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }, delay, interval, unit)
}

inline fun runAsync(crossinline task: () -> Unit) {
    GlobalScope.launch {
        try {
            task()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}