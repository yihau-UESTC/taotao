package com.taotao.serach.service;

import com.taotao.pojo.SerachItem;
import com.taotao.pojo.SerachResult;
import com.taotao.pojo.TaotaoResult;

public interface SerachItemService {
    TaotaoResult importAllItems() throws Exception;
    SerachResult serach(String queryString, int page, int rows)throws Exception;
    SerachItem importAddItem(long id) throws Exception;
}
