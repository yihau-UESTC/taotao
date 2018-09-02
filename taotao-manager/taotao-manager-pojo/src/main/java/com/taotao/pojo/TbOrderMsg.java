package com.taotao.pojo;

import java.io.Serializable;

public class TbOrderMsg implements Serializable {
    private long id;
    private long itemId;
    private int num;

    public TbOrderMsg() {
    }

    public TbOrderMsg(long id, long itemId, int num) {
        this.id = id;
        this.itemId = itemId;
        this.num = num;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
