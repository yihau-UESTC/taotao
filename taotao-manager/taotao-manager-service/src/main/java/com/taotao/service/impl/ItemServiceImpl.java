package com.taotao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.dao1.TbItemDescMapper;
import com.taotao.dao1.TbItemMapper;
import com.taotao.dao1.TbOrderMsgMapper;
import com.taotao.jedis.JedisClient;
import com.taotao.pojo.*;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Autowired
    private TbOrderMsgMapper orderMsgMapper;
    @Autowired
    private JedisClient jedisClient;

    @Resource(name="itemAddTopic")
    private Destination destination;

    private final String ITEM = "ITEM";


    @Cacheable(value = ITEM, key = "'ITEM_INFO:' + #itemId + ':BASE'")
    @Override
    public TbItem getItemById(long itemId) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
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
    @Caching(evict = {
            @CacheEvict(value = ITEM, key = "'ITEM_INFO:' + #item.id + ':BASE'"),
            @CacheEvict(value = ITEM, key = "'ITEM_INFO:' + #item.id + ':DESC'")
    })
    @Override
    public TaotaoResult saveItem(TbItem item, String desc){
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
        //发送消息队列
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                TextMessage textMessage = session.createTextMessage(id + "");
                return textMessage;
            }
        });
        return TaotaoResult.ok();
    }

    @Cacheable(value = ITEM, key = "'ITEM_INFO:' + #itemId + ':DESC'")
    @Override
    public TbItemDesc getItemDescById(long itemId) {
        TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
        return tbItemDesc;
    }

    @Override
    public TaotaoResult queryItemNum(List<TbItem> items) {
        TaotaoResult result = null;
        for (TbItem item : items){
            long itemId = item.getId();
            TbItem itemOld = itemMapper.selectByPrimaryKey(itemId);
            if (itemOld.getNum() < item.getNum()){
                result = TaotaoResult.build(200, "库存不足", item.getId());
                return result;
            }
        }
        result = TaotaoResult.build(200, "库存充足", null);
        return result;
    }

    @Override
    public TaotaoResult reduceItemNum(List<TbItem> items) {
        TaotaoResult result = null;
        for (TbItem item : items){
            long itemId = item.getId();
            TbItem itemOld = itemMapper.selectByPrimaryKey(itemId);
            if (itemOld.getNum() < item.getNum()){
                result = TaotaoResult.build(200, "库存不足", item.getId());
                return result;
            }
            itemOld.setNum(itemOld.getNum() - item.getNum());
            itemMapper.updateByPrimaryKey(itemOld);
        }
        if (items.size() > 0){
            TbItem item = items.get(0);
            TbOrderMsg msg = new TbOrderMsg();
            if (!jedisClient.exists("ORDER_ID_GEN")){
                jedisClient.set("ORDER_ID_GEN", "10054");
            }
            long orderId = jedisClient.incr("ORDER_ID_GEN");
            msg.setId(orderId);
            msg.setItemId(item.getId());
            msg.setNum(item.getNum());
            orderMsgMapper.insertOrderMsg(msg);
        }
        result = TaotaoResult.build(200, "库存充足", null);
        return result;
    }
}
