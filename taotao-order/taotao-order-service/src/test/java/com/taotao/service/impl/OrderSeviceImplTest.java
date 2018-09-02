package com.taotao.service.impl;

import com.taotao.dao1.TbOrderMsgMapper;
import com.taotao.dao2.TbOrderMapper;
import com.taotao.pojo.TbOrder;
import com.taotao.pojo.TbOrderMsg;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class OrderSeviceImplTest {


    private ClassPathXmlApplicationContext context;

    @Before
    public void init(){
        context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
    }


    @Test
    public void insertOrderInfo() {
        TbOrderMapper orderMapper = context.getBean(TbOrderMapper.class);
        TbOrderMsgMapper orderMsgMapper = context.getBean(TbOrderMsgMapper.class);
        TbOrder order = new TbOrder();
        order.setOrderId("11234577");
        orderMapper.insert(order);

        TbOrderMsg orderMsg = new TbOrderMsg();
        orderMsg.setId(123344L);
        orderMsgMapper.insertOrderMsg(orderMsg);
    }
}