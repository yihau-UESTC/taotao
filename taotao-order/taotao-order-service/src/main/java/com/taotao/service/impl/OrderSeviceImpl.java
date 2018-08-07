package com.taotao.service.impl;

import com.taotao.dao.TbItemMapper;
import com.taotao.dao.TbOrderItemMapper;
import com.taotao.dao.TbOrderMapper;
import com.taotao.dao.TbOrderShippingMapper;
import com.taotao.pojo.*;
import com.taotao.service.OrderService;
import com.taotao.service.jedis.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Date;
import java.util.List;
@Service
public class OrderSeviceImpl implements OrderService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination orderTopic;

    @Override
    public TaotaoResult createOrder(final OrderInfo orderInfo) {
        if (!jedisClient.exists("ORDER_ID_GEN")){
            jedisClient.set("ORDER_ID_GEN", "10054");
        }
        String orderId = jedisClient.incr("ORDER_ID_GEN").toString();
        orderInfo.setOrderId(orderId);
        Date date = new Date();
        orderInfo.setCreateTime(date);
        orderInfo.setUpdateTime(date);
        jmsTemplate.send(orderTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(orderInfo);
                return objectMessage;
            }
        });
        return TaotaoResult.ok(orderId);
    }
    @Override
    public void insertOrderInfo(OrderInfo info) throws ArithmeticException{
       try {
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
           TbOrderShipping orderShipping = info.getOrderShipping();
           orderShipping.setOrderId(info.getOrderId());
           orderShippingMapper.insert(orderShipping);
       }catch (ArithmeticException e){
           throw new ArithmeticException(e.getMessage());
       }
    }

    @Override
    public TaotaoResult cancelOrder(OrderInfo orderInfo) {
        //将库存加回去
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        if (orderItems != null){
            for (TbOrderItem item : orderItems){
                long itemId = Long.parseLong(item.getItemId());
                int num = item.getNum();
                TbItem itemOld = itemMapper.selectByPrimaryKey(itemId);
                itemOld.setNum(itemOld.getNum() + num);
                itemMapper.updateByPrimaryKey(itemOld);
            }
        }
        return TaotaoResult.ok();
    }
}
