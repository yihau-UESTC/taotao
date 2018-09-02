package com.taotao.service;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

import java.util.List;

public interface ItemService {
    TbItem getItemById(long itemId);
    EasyUIDataGridResult getItemList(int page, int rows);
    TaotaoResult saveItem(TbItem item, String desc)throws Throwable;
    TbItemDesc getItemDescById(long itemId);
    TaotaoResult queryItemNum(List<TbItem> items);
    TaotaoResult reduceItemNum(List<TbItem> items);
}
