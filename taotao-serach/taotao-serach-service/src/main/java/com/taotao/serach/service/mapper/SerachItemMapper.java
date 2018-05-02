package com.taotao.serach.service.mapper;

import com.taotao.pojo.SerachItem;
import java.util.List;

public interface SerachItemMapper {
    List<SerachItem> getItemList();
    SerachItem getItemById(long id);
}
