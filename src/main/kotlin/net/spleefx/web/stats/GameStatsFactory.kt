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

import net.spleefx.web.scheduleAsync
import net.spleefx.web.util.IncrementingID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object GameStatsFactory {

    private val id = IncrementingID("gameStats")
    private val storageFactory = RedisStorageFactory()

    fun createGame(payload: String): Int {
        val id = id.nextID
        storageFactory[id] = payload
        return id
    }

    fun readGame(id: Int): CompletableFuture<String?> {
        return storageFactory[id]
    }

    fun schedule() {
        scheduleAsync(1, 1, TimeUnit.HOURS) {
            storageFactory.asyncSave()
        }
    }

    fun save() = storageFactory.save()

}