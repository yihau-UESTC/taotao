package com.taotao.controller;

import com.taotao.pojo.SerachResult;
import com.taotao.serach.service.SerachItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;

@Controller
public class SerachController {

    @Autowired
    private SerachItemService serachItemService;

    @RequestMapping("/search")
    public String serach(@RequestParam("q")String queryString, @RequestParam(defaultValue = "1")int page, Model model) throws Exception {
        String transString = new String(queryString.getBytes("iso-8859-1"), "utf-8");
        SerachResult serachResult = serachItemService.serach(transString, page, 20);
        model.addAttribute("totalPages", serachResult.getTotalPages());
        model.addAttribute("query", transString);
        model.addAttribute("itemList", serachResult.getItemList());
        model.addAttribute("page", page);
        return "search";
    }
}
