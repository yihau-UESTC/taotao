package com.taotao.controller;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.OrderService;
import com.taotao.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/order-cart")
    public String showOrder(HttpServletRequest request, Model model){
        //用户登录拦截
        //从cookie取出商品列表
        List<TbItem> itemList = getItemList(request);
        model.addAttribute("cartList", itemList);
        return "order-cart";
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

    @RequestMapping("/order/create")
    public String createOrder(OrderInfo orderInfo, Model model){
        TaotaoResult result = orderService.creatOrder(orderInfo);
        model.addAttribute("orderId",result.getData());
        model.addAttribute("payment", orderInfo.getPayment());
        DateTime dateTime =new DateTime();
        dateTime.plusDays(3);
        model.addAttribute("date",dateTime.toString());
        return "success";
    }
}
