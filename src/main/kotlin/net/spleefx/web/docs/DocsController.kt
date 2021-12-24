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

import net.spleefx.web.util.GSON
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import java.util.concurrent.CompletableFuture

@RestController
class DocsController {

    @GetMapping("/wiki/search")
    fun search(): ModelAndView {
        return ModelAndView("search.html")
            .addObject("pagesCount", DocumentCache.pages.size)
            .addObject("sidebar", DocumentCache.getPage("_Sidebar"))
    }

    @GetMapping("/wiki/{name}")
    fun getDocumentRaw(@PathVariable name: String): ModelAndView {
        if (name.startsWith("_")) // pages that start with _ aren't actual pages. don't expose those :D
            return ModelAndView("errors/404.html")
        return DocumentCache.getPage(name).let {
            if (it == "404.html") return@let ModelAndView("errors/404.html")
            try {
                ModelAndView("wiki-template.html")
                    .addObject("pageTitle", name.replace('-', ' '))
                    .addObject("pageContent", it)
                    .addObject("pageID", name)
                    .addObject("sidebar", DocumentCache.getPage("_Sidebar"))
                    .addObject("footer", DocumentCache.getPage("_Footer"))
            } catch (t: Throwable) {
                ModelAndView("errors/404.html")
            }
        }
    }

    @Async
    @GetMapping(path = ["/search/raw"], produces = ["application/json"])
    fun searchRaw(
        @RequestParam(defaultValue = "true") ignoreCase: Boolean,
        @RequestParam(defaultValue = "false") titleOnly: Boolean,
        @RequestParam query: String
    ): CompletableFuture<Map<String, String>> {
        return CompletableFuture.completedFuture(
            DocumentCache.search(
                query = query,
                ignoreCase = ignoreCase,
                titlesOnly = titleOnly
            )
        )
    }

    @Async
    @GetMapping(path = ["/search"])
    fun search(
        @RequestParam query: String,
        @RequestParam(defaultValue = "true") ignoreCase: Boolean,
        @RequestParam(defaultValue = "false") titlesOnly: Boolean
    ): CompletableFuture<ModelAndView> {
        val results = DocumentCache.search(query = query, ignoreCase = ignoreCase, titlesOnly = titlesOnly)
        return CompletableFuture.completedFuture(
            ModelAndView("search_results.html")
                .addObject("results", GSON.toJson(results))
                .addObject("query", query)
                .addObject("resultCount", results.size)
                .addObject("sidebar", DocumentCache.getPage("_Sidebar"))
        )
    }
}