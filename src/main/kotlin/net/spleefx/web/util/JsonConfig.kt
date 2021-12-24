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
package net.spleefx.web.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.spleefx.web.DIR
import java.io.File

class JsonConfig(file: String) {

    private val file = File(DIR, file).also {
        if (!it.exists()) {
            it.createNewFile()
            it.writeText("{}")
        }
    }

    var json = this.file.bufferedReader().use { JsonParser.parseReader(it)!! }
        private set

    fun reload() {
        json = file.bufferedReader().use { JsonParser.parseReader(it) }
    }

    fun save() {
        file.writeText(GSON.toJson(json))
    }

    fun string(key: String): String {
        println(key)
        println(file.absolutePath)
        return json.asJsonObject[key]!!.asString
    }

    fun int(key: String): Int = json[key]!!.asInt
    fun stringList(key: String): List<String> = json[key]!!.asJsonArray.map { it.asString }

    operator fun set(key: String, element: JsonElement) {
        json[key] = element
    }
}

operator fun JsonElement.get(key: String): JsonElement? {
    return asJsonObject[key]
}

operator fun JsonElement.set(key: String, element: JsonElement) = asJsonObject.add(key, element)
val GSON = GsonBuilder().setPrettyPrinting().create()