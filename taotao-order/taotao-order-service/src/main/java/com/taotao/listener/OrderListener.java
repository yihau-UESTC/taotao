package com.taotao.listener;


import com.taotao.pojo.TbOrderMsg;
import com.taotao.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashSet;
import java.util.Set;

/**
 * 订单消费者的处理逻辑，
 * 生成订单加入订单库
 * 记录消息的id防止重复消费
 */
public class OrderListener implements MessageListener {
    @Autowired
    private OrderService orderService;
    private static Set<Long> msgCache = new HashSet<>();

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                TbOrderMsg msg = (TbOrderMsg) objectMessage.getObject();
                synchronized (msgCache) {
                    if (msgCache.contains(msg.getId()))
                        return;
                    orderService.insertOrderInfo(msg);
                    msgCache.add(msg.getId());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }
    }

}
