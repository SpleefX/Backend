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
package net.spleefx.web.docs

import net.spleefx.web.WIKI
import net.spleefx.web.util.content
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max

object DocumentCache {

    val pages = ConcurrentHashMap<String, String>()

    fun loadAll() {
        for (file in WIKI.listFiles(FileFilter { it.name.endsWith(".md") })!!) {
            load(file)
        }
    }

    fun getPage(name: String): String? {
        return pages[name]
    }

    fun search(query: String, titlesOnly: Boolean = false, ignoreCase: Boolean = true): Map<String, String> {
        val found = LinkedHashMap<String, String>()
        for (page in pages) {
            if (page.key == "Home" || page.key.startsWith("_")) continue // don't search these pages
            val search = if (titlesOnly) page.key else page.value
            if (search.contains(query, ignoreCase))
                if (titlesOnly) found[page.key] = page.key
                else found[page.key] = computeDescription(page.value, query, ignoreCase)
        }
        return found
    }

    private fun computeDescription(content: String, query: String, ignoreCase: Boolean): String {
        val queryIndex = content.indexOf(query, ignoreCase = ignoreCase)
        var lookingForFirstSpace = true
        var lookingForLastSpace = false
        var startIndex = queryIndex
        var lastIndex = queryIndex
        var chars = 350
        for ((index, char) in content.substring(max(queryIndex - 340, 0)).withIndex()) {
            if (char == ' ') {
                if (lookingForFirstSpace) {
                    lookingForFirstSpace = false
                    startIndex = index
                    continue
                }
                if (lookingForLastSpace) {
                    lastIndex = index
                    break
                }
            }
            if (chars > 0) chars--
            if (chars <= 0) {
                lookingForLastSpace = true
            }
        }
        return "...${content.substring(startIndex, lastIndex)}..."
    }

    fun load(page: File) {
        try {
            val name = page.nameWithoutExtension
            if (name.startsWith("_"))
                pages[name] = page.content().replace("<br>", "")
            else
                pages[name] = page.content()
        } catch (ignored: FileNotFoundException) {

        }
    }

}