package com.bonc.uni.usou.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yedunyao on 2017/11/20.
 */
@RestController
@CrossOrigin
@RequestMapping("/index")
public class HelloController {

    @RequestMapping("")
    public String Hello() {
        return "Hello, welcome to use usou!";
    }

}
