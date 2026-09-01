package com.daniel_havlin.url_shortener_java.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String showHomepage() {
        return "index";
    }

    @GetMapping("/showAll")
    public String showAllUrls(Model model) {
        // we will create a page that shows every created url and short code
        // create a function in the service that fetches every url and feed it into a model
        // maybe it doesn't need to be a map i'm not that good with thymeleaf
        //model.addAttribute("", map of all urls and short codes)
        return  "showAll";
    }
}
