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
package net.spleefx.web.debug

import net.spleefx.web.ratelimit.RateLimit
import net.spleefx.web.ratelimit.RequestsLimit
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.Duration
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest

@Controller
class DebugController {

    // 5 requests per minute per IP
    private val ratelimit = RequestsLimit { RateLimit(allows = 5, every = Duration.ofMinutes(1)) }

    /**
     * Returns the static HTML page for creating a paste
     */
    @RequestMapping("/debug")
    fun index(): String {
        return "404.html"
    }

    @Async
    @PostMapping(value = ["/createDebug"], consumes = ["application/json"], produces = ["application/json"])
    fun createReport(
        @RequestBody body: DebugBody,
        servlet: HttpServletRequest
    ): CompletableFuture<ResponseEntity<DebugResponse>> {
        println(body.json)
        return ratelimit.consume(1, servlet) {
            DebugRedisAccessor.createReport(body.json).thenApply { ResponseEntity(it, HttpStatus.OK) }
        }
    }

    @Async
    @GetMapping(path = ["/debug"])
    fun viewPaste(@RequestParam id: String): CompletableFuture<ModelAndView> {
        return DebugRedisAccessor.getReport(id).thenApply {
            ModelAndView("view-debug.html").addObject("id", id).addObject("cont", it)
        }.toCompletableFuture()
    }
}