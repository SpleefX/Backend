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
package net.spleefx.web.paste

import net.spleefx.web.MAPPER
import net.spleefx.web.ratelimit.RateLimit
import net.spleefx.web.ratelimit.RequestsLimit
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import java.net.URI
import java.time.Duration
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest

/**
 * Controller for paste endpoints mappings
 */
@Controller
class PasteController {

    // 5 requests per minute per IP
    private val ratelimit = RequestsLimit { RateLimit(allows = 5, every = Duration.ofMinutes(1)) }

    /**
     * Returns the static HTML page for creating a paste
     */
    @RequestMapping("/paste")
    fun index(): String {
        return "paste.html"
    }

    /**
     * Creates a paste through the POST request.
     */
    @Async
    @PostMapping(value = ["/paste"], consumes = ["application/json"], produces = ["application/json"])
    fun createPaste(
        @RequestBody paste: PasteBody,
        servlet: HttpServletRequest
    ): CompletableFuture<ResponseEntity<String>> {
        return ratelimit.consume(1, servlet) {
            PasteFactory.createPaste(paste.paste)
                .thenApply { PasteResponse(it) }
                .thenApply { p -> ResponseEntity(MAPPER.writeValueAsString(p), HttpStatus.OK) }
        }
    }

    /**
     * Views a paste through the GET request.
     */
    @Async
    @RequestMapping(path = ["/paste/raw/{pasteId}"], produces = ["text/plain"])
    fun viewRawPaste(@PathVariable pasteId: String): CompletableFuture<ResponseEntity<String>> {
        return if (pasteId.isEmpty() || pasteId.contains("paste")) { // blame spring for not redirecting /paste/ to /paste.
            CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/paste")).build()
            )
        } else try {
            PasteFactory.readPaste(pasteId).thenApply { ResponseEntity(it, HttpStatus.OK) }
        } catch (e: PasteFactory.InvalidPasteException) {
            CompletableFuture.completedFuture(ResponseEntity("Invalid paste: " + e.message, HttpStatus.BAD_REQUEST))
        }
    }

    @Async
    @RequestMapping(path = ["/paste/{pasteId}"])
    fun viewPaste(@PathVariable pasteId: String): CompletableFuture<ModelAndView> {
        return PasteFactory.readPaste(pasteId).thenApply {
            ModelAndView("view-paste.html").addObject("id", pasteId).addObject("cont", it)
        }
    }
}