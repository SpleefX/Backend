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
package net.spleefx.web.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import net.spleefx.web.CONFIG
import net.spleefx.web.scheduleAsync
import java.util.concurrent.TimeUnit

open class RedisAccessor(databaseIndex: Int) {

    protected val whitespace = Regex.fromLiteral("\\s")

    /**
     * The internal Redis client
     */
    val redisClient: StatefulRedisConnection<String, String> = RedisClient
        .create("redis://${CONFIG.string("redisIP")}/$databaseIndex")
        .connect()

    inline fun <R> async(block: RedisAsyncCommands<String, String>.() -> R): R {
        return redisClient.async().block()
    }

    inline fun <R> sync(block: RedisCommands<String, String>.() -> R): R {
        return redisClient.sync().block()
    }

    fun save(): Unit = sync { save() }

    init {
        println("Established connection to Redis #$databaseIndex.")
        scheduleAsync(1, 1, TimeUnit.HOURS) {
            async { save() }
        }
    }
}