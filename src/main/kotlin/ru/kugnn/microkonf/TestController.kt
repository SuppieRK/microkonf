package ru.kugnn.microkonf

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.ModelAndView

@Controller
class TestController {
    @Get("/index")
    fun index(): ModelAndView<String> {
        println("Called index")
        return ModelAndView("index.html", "")
    }
}