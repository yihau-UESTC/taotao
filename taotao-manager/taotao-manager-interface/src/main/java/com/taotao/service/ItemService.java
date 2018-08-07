package com.taotao.service;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
    TbItem getItemById(long itemId);
    EasyUIDataGridResult getItemList(int page, int rows);
    TaotaoResult saveItem(TbItem item, String desc)throws Throwable;
    TbItemDesc getItemDescById(long itemId);
    TaotaoResult reduceItemNum(TbItem item);
}
