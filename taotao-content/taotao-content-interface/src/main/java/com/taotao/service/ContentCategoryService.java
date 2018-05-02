package com.taotao.service;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;

import java.util.List;

public interface ContentCategoryService {
    List<EasyUITreeNode> getContentCategoryList(long parentId);
    TaotaoResult addContentCategory(Long parentId, String name);
    TaotaoResult updateContentCategory(Long id, String name);
    TaotaoResult deleteContentCategory(Long id);
}
