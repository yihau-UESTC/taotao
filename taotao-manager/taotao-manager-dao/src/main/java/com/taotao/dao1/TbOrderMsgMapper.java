package com.taotao.dao1;

import com.taotao.pojo.TbOrderMsg;

import java.util.List;

public interface TbOrderMsgMapper {
    void insertOrderMsg(TbOrderMsg msg);
    void deleteOrderMsgByOrderId(long orderId);
    List<TbOrderMsg> selectMsg();
}
