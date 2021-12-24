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

import io.lettuce.core.RedisFuture
import net.spleefx.web.redis.RedisAccessor
import net.spleefx.web.scheduleAsync
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object DebugRedisAccessor : RedisAccessor(databaseIndex = 3) {

    fun createReport(text: String): CompletableFuture<DebugResponse> {
        val id = UUID.randomUUID().toString().replace("-", "")
        val future = CompletableFuture<DebugResponse>()
        async {
            set(id, text.replace(whitespace, ""))
                .thenRun { expire(id, TimeUnit.DAYS.toSeconds(15)) }
                .thenRun { future.complete(DebugResponse(id)) }
        }
        return future
    }

    fun getReport(id: String): RedisFuture<String> = async { get(id) }

    fun schedule() = scheduleAsync(1, 1, TimeUnit.HOURS) { save() }

}