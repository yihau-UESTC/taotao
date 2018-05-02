package com.taotao.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.dao.TbContentMapper;
import com.taotao.jedis.JedisClient;
import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.service.ContentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    public TbContentMapper contentMapper;
    @Autowired
    public JedisClient jedisClient;
    @Value("${INDEX_CONTENT}")
    public String INDEX_CONTENT;


    @Override
    public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
        //1、设置分页信息
        PageHelper.startPage(page, rows);
        //2、执行查询
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> contentList = contentMapper.selectByExample(example);
        //3、获取分页信息
        PageInfo<TbContent> pageInfo = new PageInfo<>(contentList);
        //4、创建返回结果对象
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal((int) pageInfo.getTotal());
        result.setRows(contentList);
        return result;
    }

    @Override
    public TaotaoResult addContent(TbContent tbContent) {
        Date date = new Date();
        tbContent.setCreated(date);
        tbContent.setUpdated(date);
        contentMapper.insert(tbContent);
        //缓存同步
        jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult editContent(TbContent tbContent) {
        Date date = new Date();
        tbContent.setUpdated(date);
        contentMapper.updateByPrimaryKeyWithBLOBs(tbContent);
        //缓存同步
        jedisClient.hdel(INDEX_CONTENT, tbContent.getCategoryId().toString());
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult deleteContent(String ids) {
        String[] deleteIds = ids.split(",");
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        List<Long> list = new ArrayList<>();
        for (String s : deleteIds){
            list.add(Long.parseLong(s));
        }
        criteria.andIdIn(list);
        contentMapper.deleteByExample(example);
        return TaotaoResult.ok();
    }

    @Override
    public List<TbContent> getContentList(Long categoryId) {
        try {
            String json = jedisClient.hget(INDEX_CONTENT, categoryId.toString());
            if (!StringUtils.isBlank(json)){
                return JSON.parseArray(json, TbContent.class);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> list = contentMapper.selectByExample(example);
        try{
            String json = JSON.toJSONString(list);
            jedisClient.hset(INDEX_CONTENT, categoryId.toString(), json);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
