package com.taotao.service.impl;

import com.taotao.dao1.*;
import com.taotao.dao2.TbOrderItemMapper;
import com.taotao.dao2.TbOrderMapper;
import com.taotao.dao2.TbOrderShippingMapper;
import com.taotao.pojo.*;
import com.taotao.service.OrderService;
import com.taotao.service.jedis.JedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
public class OrderSeviceImpl implements OrderService {
    Logger logger = LoggerFactory.getLogger(OrderSeviceImpl.class);

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
    @Autowired
    private TbOrderMsgMapper orderMsgMapper;

    @Deprecated
    @Override
    public TaotaoResult createOrder(final OrderInfo orderInfo) {
        if (!jedisClient.exists("ORDER_ID_GEN")) {
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

    /**
     * 在订单数据库中插入订单信息
     * @param msg
     */
    @Override
    public void insertOrderInfo(TbOrderMsg msg){
        TbOrder order = new TbOrder();
        order.setOrderId(String.valueOf(msg.getId()));
        orderMapper.insertSelective(order);
        //：TODO 补全其他订单信息
    }

    @Deprecated
    @Override
    public TaotaoResult cancelOrder(OrderInfo orderInfo) {
        //将库存加回去
        List<TbOrderItem> orderItems = orderInfo.getOrderItems();
        if (orderItems != null) {
            for (TbOrderItem item : orderItems) {
                long itemId = Long.parseLong(item.getItemId());
                int num = item.getNum();
                TbItem itemOld = itemMapper.selectByPrimaryKey(itemId);
                itemOld.setNum(itemOld.getNum() + num);
                itemMapper.updateByPrimaryKey(itemOld);
            }
        }
        return TaotaoResult.ok();
    }

    /**
     * 将本地数据库中的消息加到MQ中，然后删除该消息。
     */
    @Override
    public TaotaoResult putMsgIntoMQ() {
        List<TbOrderMsg> tbOrderMsgs = orderMsgMapper.selectMsg();
        for (final TbOrderMsg orderMsg : tbOrderMsgs) {
            jmsTemplate.send(orderTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage message = session.createObjectMessage(orderMsg);
                    return message;
                }
            });
            orderMsgMapper.deleteOrderMsgByOrderId(orderMsg.getId());
        }
        return TaotaoResult.ok();
    }

}
