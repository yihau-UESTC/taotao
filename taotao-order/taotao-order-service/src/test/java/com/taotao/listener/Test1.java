package com.taotao.listener;

import com.taotao.dao.TbOrderItemMapper;
import com.taotao.dao.TbOrderMapper;
import com.taotao.dao.TbOrderShippingMapper;
import com.taotao.dao.TbUserMapper;
import com.taotao.pojo.OrderInfo;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;
import com.taotao.pojo.TbUser;
import com.taotao.service.jedis.JedisClient;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml"})
public class Test1 {
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private SqlSessionFactory sessionFactory;

    @Test
    public void insertOrderInfo() throws SQLException {
        SqlSession session = sessionFactory.openSession();
        Connection connection = session.getConnection();
        try {

            connection.setAutoCommit(false);
            TbUserMapper mapper1 = session.getMapper(TbUserMapper.class);
            TbOrderShippingMapper mapper2 = session.getMapper(TbOrderShippingMapper.class);
            TbOrderShipping tbOrderShipping = new TbOrderShipping();
            tbOrderShipping.setOrderId("127");
            TbUser user = new TbUser();
            user.setId(127L);
            user.setUsername("asdf");
            mapper2.insert(tbOrderShipping);
//            int a = 1 / 0;
            mapper1.insert(user);
            connection.commit();

        }catch (Exception e){
            connection.rollback();
        }
    }
}