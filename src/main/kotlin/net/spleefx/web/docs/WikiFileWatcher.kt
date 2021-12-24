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
import net.spleefx.web.runAsync
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent
import java.nio.file.WatchKey

@Suppress("UNCHECKED_CAST")
object WikiFileWatcher {

    private val directory = WIKI.toPath()
    private val watcher = FileSystems.getDefault().newWatchService()

    fun watch() {
        runAsync {
            while (true) {
                try {
                    val key: WatchKey = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
                    for (event in key.pollEvents()) {
                        val kind = event.kind()
                        if (kind == OVERFLOW) continue

                        // The filename is the
                        // context of the event.
                        val ev: WatchEvent<Path> = event as WatchEvent<Path>
                        val filename: Path = ev.context()

                        // Resolve the filename against the directory.
                        // If the filename is "test" and the directory is "foo",
                        // the resolved name is "test/foo".
                        val child: Path = directory.resolve(filename)
                        if ((kind == ENTRY_CREATE) or (kind == ENTRY_MODIFY))
                            DocumentCache.load(child.toFile())
                        else
                            DocumentCache.pages.remove(child.toFile().nameWithoutExtension)
                    }
                } catch (x: IOException) {
                    System.err.println(x)
                }
            }
        }
    }

}