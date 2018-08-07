package com.taotao.controller;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/test/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId){
        System.out.println("=====================================================");
        return itemService.getItemById(itemId);
    }
    @RequestMapping("/item/testdesc/{itemId}")
    @ResponseBody
    public TbItemDesc getItemDescById(@PathVariable Long itemId){
        System.out.println("=====================================================");
        return itemService.getItemDescById(itemId);
    }

    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(Integer page, Integer rows){
        return itemService.getItemList(page, rows);
    }
    @ResponseBody
    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    public TaotaoResult saveItem(TbItem item, String desc){
        try {
            return itemService.saveItem(item, desc);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return TaotaoResult.build(400,"internel error");
        }
    }
}
