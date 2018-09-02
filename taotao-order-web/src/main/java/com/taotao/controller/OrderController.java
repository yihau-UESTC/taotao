package com.taotao.controller;

import com.alibaba.fastjson.JSON;
import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import com.taotao.service.OrderService;
import com.taotao.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;

    @RequestMapping("/order/order-cart")
    public String showOrder(HttpServletRequest request, Model model) {
        //用户登录拦截
        //从cookie取出商品列表
        List<TbItem> itemList = getItemList(request);
        //减库存
        TaotaoResult result = itemService.queryItemNum(itemList);
        if (result.getMsg().equals("库存不足")) {
            return "error";
        }
        model.addAttribute("cartList", itemList);
        return "order-cart";
    }

    private List<TbItem> getItemList(HttpServletRequest request) {
        String cartKey = CookieUtils.getCookieValue(request, "CART_KEY", true);
        if (StringUtils.isBlank(cartKey)) return new ArrayList<>();
        List<TbItem> list = JSON.parseArray(cartKey, TbItem.class);
        for (TbItem item : list) {
            String images = item.getImage();
            if (StringUtils.isBlank(images)) continue;
            String[] imageArray = images.split(",");
            item.setImage(imageArray[0]);
        }
        return list;
    }

    @RequestMapping("/order/create")
    public String createOrder(HttpServletRequest request, Model model) {
        //读商品
        List<TbItem> itemList = getItemList(request);
        //扣库存,插入本地消息表，返回页面
        TaotaoResult result = itemService.reduceItemNum(itemList);
        if (result.getMsg().equals("库存不足"))
            return "error";
        return "success";
    }

    @RequestMapping("/order/cancel")
    public String cancelOrder(OrderInfo orderInfo) {
        TaotaoResult result = orderService.cancelOrder(orderInfo);
        return "cancel";
    }
}
