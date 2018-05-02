package com.taotao.controller;

import com.alibaba.fastjson.JSON;
import com.taotao.pojo.AD1Node;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Value("${AD1_CATEGORYID}")
    private long ad1CategoryId;
    @Value("${AD1_WIDTH}")
    private int ad1Width;
    @Value("${AD1_WIDTHB}")
    private int ad1WidthB;
    @Value("${AD1_HEIGHT}")
    private int ad1Height;
    @Value("${AD1_HEIGHTB}")
    private int ad1HeightB;

    @Autowired
    private ContentService contentService;

    @RequestMapping("/index")
    public String showIndex(Model model){
        List<TbContent> contentList = contentService.getContentList(ad1CategoryId);
        List<AD1Node> ad1NodeList = new ArrayList<>();
        for (TbContent tbContent : contentList){
            AD1Node temp = new AD1Node();
            temp.setAlt(tbContent.getTitle());
            temp.setHeight(ad1Height);
            temp.setHeightB(ad1HeightB);
            temp.setHref(tbContent.getUrl());
            temp.setSrc(tbContent.getPic());
            temp.setSrcB(tbContent.getPic2());
            temp.setWidth(ad1Width);
            temp.setWidthB(ad1WidthB);
            ad1NodeList.add(temp);
        }
        //将ad1NodeList转换成json格式数据
        String ad1json = JSON.toJSONString(ad1NodeList);
        model.addAttribute("ad1", ad1json);
        return "index";
    }
}
