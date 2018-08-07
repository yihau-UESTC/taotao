package com.taotao.listener;

import com.taotao.dao.TbOrderItemMapper;
import com.taotao.dao.TbOrderMapper;
import com.taotao.dao.TbOrderShippingMapper;
import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import com.taotao.service.jedis.JedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/application*.xml"})
public class OrderListenerTest {
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Autowired
    private JedisClient jedisClient;

    @Test
    public void insertOrderInfo() {
        OrderInfo info = new OrderInfo();
        info.setOrderId("12345");
        List<TbOrderItem> orderItems1 = new ArrayList<>();
        orderItems1.add(new TbOrderItem());
        info.setOrderItems(orderItems1);
        info.setOrderShipping(new TbOrderShipping());

        orderMapper.insert(info);
        List<TbOrderItem> orderItems = info.getOrderItems();
        if (orderItems != null){
            for (TbOrderItem item : orderItems){
                item.setOrderId(info.getOrderId());
                String orderItemId = jedisClient.incr("ORDER_ITEM_ID_GEN").toString();
                item.setId(orderItemId);
                orderItemMapper.insert(item);
            }
        }
        int a = 1 / 0;
        TbOrderShipping orderShipping = info.getOrderShipping();
        orderShipping.setOrderId(info.getOrderId());
        orderShippingMapper.insert(orderShipping);
    }
}