package com.taotao.listener;


import com.taotao.dao.TbOrderItemMapper;
import com.taotao.dao.TbOrderMapper;
import com.taotao.dao.TbOrderShippingMapper;
import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import com.taotao.service.OrderService;
import com.taotao.service.jedis.JedisClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;


public class OrderListener implements MessageListener {
    @Autowired
    private OrderService orderService;
    private static long count = 0;

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage){
           ObjectMessage objectMessage = (ObjectMessage)message;
            try {
                OrderInfo info = (OrderInfo) objectMessage.getObject();
                System.out.println("count = " + count++);
                orderService.insertOrderInfo(info);
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (ArithmeticException e){
                e.printStackTrace();
                System.out.println("111111");
                throw new ArithmeticException(e.getMessage());
            }
        }
    }

}
