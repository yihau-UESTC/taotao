package com.taotao.service;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

import java.util.List;

public interface ContentService {
    EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows);
    TaotaoResult addContent(TbContent tbContent);
    TaotaoResult editContent(TbContent tbContent);
    TaotaoResult deleteContent(Long categoryId);
    Long getCategory(Long id);
    List<TbContent> getContentList(Long categoryId);
}
