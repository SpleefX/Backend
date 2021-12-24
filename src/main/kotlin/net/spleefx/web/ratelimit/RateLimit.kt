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

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * A simple ratelimiter that implements the token bucket strategy.
 *
 * See [https://en.wikipedia.org/wiki/Token_bucket]
 */
class RateLimit(allows: Long, every: Duration) {

    /**
     * The inner bucket
     */
    private val bucket: Bucket = Bucket4j.builder().addLimit(Bandwidth.classic(allows, Refill.intervally(allows, every))).build()

    /**
     * Attempts to consume 1 token from the bucket, otherwise returns a [ResponseEntity] with a status code
     * of [HttpStatus.TOO_MANY_REQUESTS].
     *
     * Wrapped in completable futures because everything we use is in completable futures, so that reduces our headache.
     */
    fun <R> consume(tokens: Long = 1, block: () -> CompletableFuture<ResponseEntity<R>>): CompletableFuture<ResponseEntity<R>> {
        val probe = bucket.tryConsumeAndReturnRemaining(tokens)
        if (probe.isConsumed) {
            return block()
        }
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header("X-Rate-Limit-Retry-After-Milliseconds", TimeUnit.NANOSECONDS.toMillis(probe.nanosToWaitForRefill).toString())
                        .build())
    }
}