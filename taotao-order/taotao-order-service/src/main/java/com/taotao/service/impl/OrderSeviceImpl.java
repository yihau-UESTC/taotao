package com.taotao.service.impl;

import com.taotao.dao.TbOrderItemMapper;
import com.taotao.dao.TbOrderMapper;
import com.taotao.dao.TbOrderShippingMapper;
import com.taotao.pojo.*;
import com.taotao.service.OrderService;
import com.taotao.service.jedis.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class OrderSeviceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private JedisClient jedisClient;

    @Override
    public TaotaoResult creatOrder(OrderInfo orderInfo) {
        if (!jedisClient.exists("ORDER_ID_GEN")){
            jedisClient.set("ORDER_ID_GEN", "10054");
        }
        String orderId = jedisClient.incr("ORDER_ID_GEN").toString();
        orderInfo.setOrderId(orderId);
        Date date = new Date();
        orderInfo.setCreateTime(date);
        orderInfo.setUpdateTime(date);
        orderMapper.insert(orderInfo);
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        for (TbOrderItem orderItem : orderItems){
            String orderItemId = jedisClient.incr("ORDER_ITEM_ID_GEN").toString();
            orderItem.setId(orderItemId);
            orderItem.setOrderId(orderId);
            orderItemMapper.insert(orderItem);
        }
        TbOrderShipping orderShipping = orderInfo.getOrderShipping();
        orderShipping.setCreated(date);
        orderShipping.setUpdated(date);
        orderShipping.setOrderId(orderId);
        orderShippingMapper.insert(orderShipping);
        return TaotaoResult.ok(orderId);
    }
}
