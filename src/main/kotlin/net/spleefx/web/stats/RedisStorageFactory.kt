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
package net.spleefx.web.stats

import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import net.spleefx.web.CONFIG
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * A simple wrapper for Redis calls to be run in asynchronous context.
 */
class RedisStorageFactory {

    /**
     * The internal Redis client
     */
    private val redisClient: StatefulRedisConnection<String, String> =
        RedisClient.create("redis://" + CONFIG.string("redisIP") + "/2").connect()

    /**
     * Updates the specified key -> value asynchronously
     */
    operator fun set(key: Any, value: String) {
        val k = key.toString()
        with(redisClient.async()) {
            set(k, value)
            expire(k, TimeUnit.DAYS.toSeconds(15))
        }
    }

    /**
     * Returns the value mapping to the specified key asynchronously. May be null.
     */
    operator fun get(key: Any): CompletableFuture<String?> {
        return redisClient.async()[key.toString()].toCompletableFuture()
    }

    /**
     * Executes the save command
     */
    fun save() {
        redisClient.sync().save()
    }

    /**
     * Executes the save command asynchronously
     */
    fun asyncSave() {
        redisClient.async().save()
    }

}