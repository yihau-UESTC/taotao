package com.taotao.handler;

import com.taotao.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 后台定时任务从消息表读出数据发到消息队列
 */
public class OrderHandler{
    Logger logger = LoggerFactory.getLogger(OrderHandler.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private ScheduledExecutorService service;

    public void initTask(){
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                orderService.putMsgIntoMQ();
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    public void stopTask(){
        service.shutdown();
    }
}
