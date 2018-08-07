package com.taotao;

import com.taotao.pojo.TbItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestList {
    @Test
    public void run(){
        TbItem item = new TbItem();
        TbItem item1 = new TbItem();
        List<TbItem> list = new ArrayList<>();
        list.add(item);
        list.add(item1);
        for (TbItem i : list){
            i.setNum(5);
        }
        System.out.println(list.toString());
    }
}
