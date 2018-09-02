package com.taotao.listener;

import com.taotao.handler.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 监听容器的刷新事件，初始化后台定时任务
 */
public class MsgPostProcessorListener implements ApplicationListener<ContextRefreshedEvent> {


    @Autowired
    public OrderHandler handler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        handler.initTask();
    }
}
