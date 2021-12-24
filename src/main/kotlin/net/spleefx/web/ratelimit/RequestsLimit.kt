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
package net.spleefx.web.ratelimit

import org.springframework.http.ResponseEntity
import java.util.Collections.synchronizedMap
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest

class RequestsLimit(private val createRateLimit: () -> RateLimit) {

    private val limits: MutableMap<String, RateLimit> = synchronizedMap(HashMap())

    fun <R> consume(
            tokens: Long = 1,
            requestIP: HttpServletRequest,
            block: () -> CompletableFuture<ResponseEntity<R>>
    ): CompletableFuture<ResponseEntity<R>> {
        return getLimit(requestIP).consume(tokens, block)
    }

    private fun getLimit(requestServlet: HttpServletRequest): RateLimit {
        return limits.computeIfAbsent(requestServlet.remoteAddr) { createRateLimit() }
    }

}