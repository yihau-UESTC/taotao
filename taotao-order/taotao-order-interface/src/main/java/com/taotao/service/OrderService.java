package com.taotao.service;

import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TaotaoResult;

public interface OrderService {
    TaotaoResult creatOrder(OrderInfo orderInfo);
}