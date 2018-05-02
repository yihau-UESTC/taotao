package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {

    @RequestMapping("/")
    public String showIndex(){
        return "index";
    }
/*
    原来的url是http://localhost:8081/item-add?_=1524052515923，
    @RequestMapping("/{page}")就只取/后面的item-add，？后面的参数可以通过get_的值得到。
 */
    @RequestMapping("/{page}")
    public String showPage(@PathVariable String page){
        return page;
    }
}
