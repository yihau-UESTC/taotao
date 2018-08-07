package com.taotao.service;

import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TaotaoResult;

public interface OrderService {
    TaotaoResult createOrder(OrderInfo orderInfo);
    void insertOrderInfo(OrderInfo info)throws ArithmeticException;
    TaotaoResult cancelOrder(OrderInfo orderInfo);
}
