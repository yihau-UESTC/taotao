package com.taotao.controller;

import com.taotao.pojo.TaotaoResult;
import com.taotao.serach.service.SerachItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private SerachItemService serachItemService;

    @RequestMapping("/index/importall")
    @ResponseBody
    public TaotaoResult importIndex() throws Exception {
        return serachItemService.importAllItems();
    }
}
