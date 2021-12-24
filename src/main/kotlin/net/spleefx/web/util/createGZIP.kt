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

import net.spleefx.web.paste.PasteFactory
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Creates a GZIP
 */
fun String.createGZIP(destination: File) {
    try {
        val written = FileOutputStream(destination)
        val zipped = GZIPOutputStream(written)
        zipped.write(toByteArray())
        zipped.finish()
        written.close()
        zipped.close()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

/**
 * Reads the GZIP text from the specified file
 */
fun File.readGZIP(): String {
    val buffer = ByteArray(1024)
    return try {
        if (!exists()) throw PasteFactory.InvalidPasteException(name)
        val fileIn = FileInputStream(this)
        val gZIPInputStream = GZIPInputStream(fileIn)
        var beingRead: Int
        val bytes = ByteArrayOutputStream()
        while (gZIPInputStream.read(buffer).also { beingRead = it } > 0) {
            bytes.write(buffer, 0, beingRead)
        }
        gZIPInputStream.close()
        bytes.close()
        String(bytes.toByteArray())
    } catch (ex: IOException) {
        ex.printStackTrace()
        throw IllegalStateException(ex)
    }
}