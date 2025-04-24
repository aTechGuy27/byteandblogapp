package com.byteandblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/", "/{path:[^\\.]*}", "/{path:[^\\.]*}/{subPath:[^\\.]*}"})
    public String redirect() {
        return "forward:/index.html";
    }
}