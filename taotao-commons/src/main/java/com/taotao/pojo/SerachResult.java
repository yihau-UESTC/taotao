package com.taotao.pojo;

import java.io.Serializable;
import java.util.List;

public class SerachResult implements Serializable {
    private int totalPages;
    private long totalItems;
    private List<SerachItem> itemList;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return  totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<SerachItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SerachItem> itemList) {
        this.itemList = itemList;
    }
}
