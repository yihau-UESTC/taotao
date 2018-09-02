package com.taotao.service;

import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbOrderMsg;

public interface OrderService {
    TaotaoResult createOrder(OrderInfo orderInfo);
    void insertOrderInfo(TbOrderMsg msg)throws ArithmeticException;
    TaotaoResult cancelOrder(OrderInfo orderInfo);
    TaotaoResult putMsgIntoMQ();
}
