package com.taotao.listener;

import com.taotao.handler.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class MsgStopListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private OrderHandler handler;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        handler.stopTask();
    }
}
