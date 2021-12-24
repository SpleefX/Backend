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
package net.spleefx.web.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
class RedirectController {

    @GetMapping("/discord")
    fun discord(): ModelAndView {
        return createRedirect("https://discord.gg/uwf72ZN")
    }

    @GetMapping("/spigot")
    fun spigot(): ModelAndView {
        return createRedirect("https://www.spigotmc.org/resources/73093/")
    }

    @GetMapping("/update/{updateID}")
    fun update(@PathVariable updateID: String): ModelAndView {
        return createRedirect("https://www.spigotmc.org/resources/73093/update?update=$updateID")
    }

    @GetMapping("/updates")
    fun updates(): ModelAndView {
        return createRedirect("https://www.spigotmc.org/resources/73093/updates")
    }

    @GetMapping("/wiki")
    fun wiki(): ModelAndView {
        return createRedirect("/wiki/Home")
    }

    @GetMapping("/github")
    fun gitHub(): ModelAndView {
        return createRedirect("https://github.com/SpleefX/")
    }

    private fun createRedirect(url: String): ModelAndView {
        return ModelAndView("redirect:$url")
    }

}