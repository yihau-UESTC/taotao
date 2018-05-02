package com.taotao.controller;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @ResponseBody
    @RequestMapping("/content/category/list")
    public List<EasyUITreeNode> getContenCategoryList(@RequestParam(value = "id",defaultValue = "0")Long parentId){
        return contentCategoryService.getContentCategoryList(parentId);
    }

    @RequestMapping("/content/category/create")
    @ResponseBody
    public TaotaoResult addContentCategory(Long parentId, String name){
        return contentCategoryService.addContentCategory(parentId, name);
    }

    @RequestMapping("/content/category/update")
    @ResponseBody
    public TaotaoResult updateContentCategory(Long id, String name){
        return contentCategoryService.updateContentCategory(id, name);
    }
    @RequestMapping("/content/category/delete/")
    @ResponseBody
    public TaotaoResult deleteContentCategory(Long id, Long parentId){
        return contentCategoryService.deleteContentCategory(id);
    }
}
