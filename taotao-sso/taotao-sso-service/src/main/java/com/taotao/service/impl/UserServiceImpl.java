package com.taotao.service.impl;

import com.alibaba.fastjson.JSON;
import com.taotao.dao1.TbUserMapper;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.service.UserService;
import com.taotao.service.jedis.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;

    @Override
    public TaotaoResult checkData(String param, int type) {
        //username
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        if (type == 1){
            criteria.andUsernameEqualTo(param);
        }else if (type == 2){//phone
            criteria.andPhoneEqualTo(param);
        }else if (type == 3){//email
            criteria.andEmailEqualTo(param);
        }else {
            return TaotaoResult.build(400, "非法类型", false);
        }
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        if (tbUsers != null && tbUsers.size() > 0)return TaotaoResult.build(400,"参数校验失败",false);
        return TaotaoResult.ok(true);
    }

    @Override
    public TaotaoResult register(TbUser user) {
        if (StringUtils.isBlank(user.getUsername())){
            return TaotaoResult.build(400, "用户名为空");
        }
        TaotaoResult result = checkData(user.getUsername(), 1);
        if (!(boolean)result.getData()){
            return TaotaoResult.build(400,"用户名重复");
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            TaotaoResult result1 = checkData(user.getPhone(), 2);
            if (!(boolean) result1.getData()) {
                return TaotaoResult.build(400, "电话号码重复");
            }
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            TaotaoResult result2 = checkData(user.getEmail(), 3);
            if (!(boolean) result2.getData()) {
                return TaotaoResult.build(400, "邮箱重复");
            }
        }
        if (StringUtils.isBlank(user.getPassword())){
            return TaotaoResult.build(400, "密码为空");
        }
        Date date = new Date();
        user.setCreated(date);
        user.setUpdated(date);
        String MD5pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(MD5pwd);
        userMapper.insert(user);
        return TaotaoResult.ok(true);
    }

    @Override
    public TaotaoResult login(String username, String password, String ip) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> tbUsers = userMapper.selectByExample(example);
        if (tbUsers != null && tbUsers.size() > 0){
            TbUser user = tbUsers.get(0);
            String MD5pwd = DigestUtils.md5DigestAsHex(password.getBytes());
            if (MD5pwd.equals(user.getPassword())){
                user.setPassword(null);
                String uuid = UUID.randomUUID().toString();
                //TODO:HSET 加入ip信息
                jedisClient.set("TT_TOKEN:" + uuid, JSON.toJSONString(user));
                jedisClient.expire("TT_TOKEN:" + uuid, 1800);
                return TaotaoResult.ok(uuid);
            }
            return TaotaoResult.build(400, "用户名或密码错误");
        }

        return TaotaoResult.build(400, "用户名或密码错误");
    }

    @Override
    public TaotaoResult getUserByToken(String token) {
        String result = jedisClient.get("TT_TOKEN:" + token);
        if (StringUtils.isBlank(result))return TaotaoResult.build(400,"用户未登录");
        jedisClient.expire("TT_TOKEN:" + token, 1800);
        TbUser user = JSON.parseObject(result, TbUser.class);
        return TaotaoResult.ok(user);
    }

    @Override
    public TaotaoResult logout(String token) {
        String result = jedisClient.get("TT_TOKEN:" + token);
        if (StringUtils.isBlank(result))return TaotaoResult.build(400,"用户未登录");
        jedisClient.del("TT_TOKEN:" + token);
        return TaotaoResult.ok();
    }
}
