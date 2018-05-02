package com.taotao.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.dao.TbItemDescMapper;
import com.taotao.dao.TbItemMapper;
import com.taotao.jedis.JedisClient;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemDescMapper itemDescMapper;
    @Autowired
    private JmsTemplate jmsTemplate;

    @Resource(name="itemAddTopic")
    private Destination destination;

    @Autowired
    private JedisClient jedisClient;

    @Override
    public TbItem getItemById(long itemId) {
        try{
            String str = jedisClient.get("ITEM_INFO:" + itemId + ":BASE");
            if (!StringUtils.isBlank(str)){
                TbItem result = JSON.parseObject(str, TbItem.class);
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        try{
            //1、加入缓存，2、设置过期时间
            jedisClient.set("ITEM_INFO:" + itemId + ":BASE", JSON.toJSONString(tbItem));
            jedisClient.expire("ITEM_INFO:" + itemId + ":BASE",86400);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tbItem;
    }

    @Override
    public EasyUIDataGridResult getItemList(int page, int rows) {
        PageHelper.startPage(page, rows);
        List<TbItem> list = itemMapper.selectByExample(new TbItemExample());
        //取查询结果
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal((int) pageInfo.getTotal());
        result.setRows(list);
        return result;
    }

    @Override
    public TaotaoResult saveItem(TbItem item, String desc) {
        //生成商品id，使用时间戳 + 两位的随机数
        final long id = IDUtils.genItemId();
        item.setId(id);
        //设置商品状态1、正常，2、下架，3、删除
        item.setStatus((byte) 1);
        Date date = new Date();
        item.setCreated(date);
        item.setUpdated(date);
        //商品表插入数据
        itemMapper.insert(item);
        //描述表插入数据
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(id);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        itemDescMapper.insert(tbItemDesc);

        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                TextMessage textMessage = session.createTextMessage(id + "");
                return textMessage;
            }
        });
        return TaotaoResult.ok();
    }

    @Override
    public TbItemDesc getItemDescById(long itemId) {
        try{
            String str = jedisClient.get("ITEM_INFO:" + itemId + ":DESC");
            if (!StringUtils.isBlank(str)){
                TbItemDesc result = JSON.parseObject(str, TbItemDesc.class);
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        try{
            //1、加入缓存，2、设置过期时间
            jedisClient.set("ITEM_INFO:" + itemId + ":DESC", JSON.toJSONString(tbItemDesc));
            jedisClient.expire("ITEM_INFO:" + itemId + ":DESC",86400);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tbItemDesc;
    }


}
