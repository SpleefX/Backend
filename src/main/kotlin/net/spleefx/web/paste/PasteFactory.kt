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

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import net.spleefx.web.*
import net.spleefx.web.util.createGZIP
import net.spleefx.web.util.readGZIP
import java.io.File
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

object PasteFactory {

    /**
     * A list of all characters used in a generated paste ID
     */
    private val idCharacters: CharArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray()

    /**
     * The random used to generate IDs
     */
    private val idRandom: Random = SecureRandom()

    /**
     * The maximum ID length
     */
    private const val ID_LENGTH = 8

    /**
     * The asynchronous cache for loading pastes
     */
    private val cache: AsyncLoadingCache<String, String> = Caffeine.newBuilder()
            .expireAfterAccess(6, TimeUnit.HOURS)
            .executor(EXECUTOR)
            .buildAsync(CacheLoader {
                try {
                    File(PASTE, "$it.gzip").readGZIP()
                } catch (e: InvalidPasteException) {
                    "Invalid paste: $it"
                }
            })

    /**
     * Creates a new paste
     *
     * @param text Paste text
     * @return A future for the paste ID
     */
    fun createPaste(text: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        runAsync {
            val id = generatePasteID()
            val file = File(PASTE, "$id.gzip")
            text.createGZIP(file)
            future.complete(id)
        }
        return future
    }

    /**
     * Reads the paste with the specified ID
     */
    fun readPaste(id: String): CompletableFuture<String> {
        return cache.get(id)
    }

    /**
     * Generates a pseudo-random paste ID on request.
     */
    private fun generatePasteID(): String {
        val id = StringBuilder()
        for (i in 0 until ID_LENGTH) {
            id.append(idCharacters[idRandom.nextInt(idCharacters.size)])
        }
        return id.toString()
    }

    /**
     * Thrown as a way to handle invalid pastes
     */
    class InvalidPasteException(id: String) : Exception(id)

}