package com.taotao.controller;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    @ResponseBody
    @RequestMapping("/content/query/list")
    public EasyUIDataGridResult getContentList(@RequestParam(defaultValue = "0") Long categoryId, Integer page, Integer rows){
        return contentService.getContentList(categoryId, page, rows);
    }
    @ResponseBody
    @RequestMapping("/content/save")
    public TaotaoResult addContent(TbContent content){
        return contentService.addContent(content);
    }
    @ResponseBody
    @RequestMapping("/content/delete")
    public TaotaoResult deleteContent(String ids){
        return contentService.deleteContent(ids);
    }

    @ResponseBody
    @RequestMapping("/content/edit")
    public TaotaoResult editContent(TbContent content){
        return contentService.editContent(content);
    }
}
