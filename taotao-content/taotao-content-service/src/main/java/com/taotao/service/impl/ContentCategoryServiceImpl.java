package com.taotao.service.impl;

import com.taotao.dao1.TbContentCategoryMapper;
import com.taotao.pojo.*;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Override
    public List<EasyUITreeNode> getContentCategoryList(long parentId) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        List<EasyUITreeNode> result = new ArrayList<>();
        for (TbContentCategory tbContentCategory : list) {
            EasyUITreeNode treeNode = new EasyUITreeNode();
            treeNode.setId(tbContentCategory.getId());
            treeNode.setText(tbContentCategory.getName());
            treeNode.setState(tbContentCategory.getIsParent() ? "closed" : "open");
            result.add(treeNode);
        }
        return result;
    }

    @Override
    public TaotaoResult addContentCategory(Long parentId, String name) {
        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setParentId(parentId);
        tbContentCategory.setName(name);
        tbContentCategory.setSortOrder(1);
        //1、正常， 2、删除
        tbContentCategory.setStatus(1);
        tbContentCategory.setIsParent(false);
        Date date = new Date();
        tbContentCategory.setCreated(date);
        tbContentCategory.setUpdated(date);
        tbContentCategoryMapper.insert(tbContentCategory);
        TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()) {
            parent.setIsParent(true);
            tbContentCategoryMapper.updateByPrimaryKey(parent);
        }
        return new TaotaoResult(tbContentCategory);
    }

    @Override
    public TaotaoResult updateContentCategory(Long id, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setId(id);
        contentCategory.setName(name);
        tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteContentCategory(Long id) {
        TbContentCategory current = tbContentCategoryMapper.selectByPrimaryKey(id);
        reDeleteContenCategory(id);
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(current.getParentId());
        List<TbContentCategory> childList = tbContentCategoryMapper.selectByExample(example);
        if (childList.size() == 0){
            TbContentCategory update = new TbContentCategory();
            update.setId(current.getParentId());
            update.setIsParent(false);
            tbContentCategoryMapper.updateByPrimaryKeySelective(update);
        }
        return TaotaoResult.ok();

    }

    private void reDeleteContenCategory(Long id) {
        TbContentCategory current = tbContentCategoryMapper.selectByPrimaryKey(id);
        if (current.getIsParent()) {
            TbContentCategoryExample example = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdEqualTo(current.getId());
            List<TbContentCategory> childList = tbContentCategoryMapper.selectByExample(example);

            for (TbContentCategory temp : childList) {
                reDeleteContenCategory(temp.getId());
            }
            tbContentCategoryMapper.deleteByPrimaryKey(current.getId());
        } else {
            tbContentCategoryMapper.deleteByPrimaryKey(id);
        }
    }
}
