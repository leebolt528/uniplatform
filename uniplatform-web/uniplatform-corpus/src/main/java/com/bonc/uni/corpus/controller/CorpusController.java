package com.bonc.uni.corpus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @RequestMapping
    public String hello() {
        return "Hello corpus!!";
    }
}
