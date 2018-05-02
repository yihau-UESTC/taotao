package com.taotao.controller;

import com.alibaba.fastjson.JSON;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import com.taotao.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private ItemService itemService;

    @RequestMapping("/cart/add/{itemId}")
    public String add2Cart(@PathVariable Long itemId, @RequestParam Integer num, HttpServletRequest request, HttpServletResponse response){
        //从cookies中取出购物车的信息
        List<TbItem> list = getItemList(request);
        //判断购物车中是否有相同的商品，有的话num++，没有增加商品
        boolean flag = false;
        for (TbItem item : list){
            if (item.getId() == itemId.longValue()){
                item.setNum(item.getNum() + num);
                flag = true;
                break;
            }
        }
        if (!flag){
            TbItem addItem = itemService.getItemById(itemId);
            addItem.setNum(num);
            list.add(addItem);
        }
        //将购物车写会cookies，返回success页面
        String json = JSON.toJSONString(list);
        CookieUtils.setCookie(request,response,"CART_KEY", json, 604800,true);
        return "cartSuccess";
    }

    @RequestMapping("/cart/cart")
    public String showCart(HttpServletRequest request, Model model){
        //从cookies中拿出商品列表
        List<TbItem> list = getItemList(request);
        model.addAttribute("cartList", list);
        return "cart";
    }

    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public TaotaoResult updateItemNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request, HttpServletResponse response){
        List<TbItem> list = getItemList(request);
        for (TbItem item : list){
            if (item.getId() == itemId.longValue()){
                item.setNum(num);
                break;
            }
        }
        CookieUtils.setCookie(request,response, "CART_KEY", JSON.toJSONString(list), 608400,true);
        return TaotaoResult.ok();
    }

    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response){
        List<TbItem> list = getItemList(request);
        for (TbItem item : list){
            if (item.getId() == itemId.longValue()){
                list.remove(item);
                break;
            }
        }
        CookieUtils.setCookie(request,response,"CART_KEY", JSON.toJSONString(list), 608400, true);
        return "redirect:/cart/cart";
    }



    private List<TbItem> getItemList(HttpServletRequest request){
        String cartKey = CookieUtils.getCookieValue(request, "CART_KEY",true);
        if (StringUtils.isBlank(cartKey))return new ArrayList<>();
        List<TbItem> list = JSON.parseArray(cartKey,TbItem.class);
        for (TbItem item : list){
            String images = item.getImage();
            if (StringUtils.isBlank(images))continue;
            String[] imageArray = images.split(",");
            item.setImage(imageArray[0]);
        }
        return list;
    }
}
