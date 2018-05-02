package com.taotao.service;

import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {
    TaotaoResult checkData(String param, int type);
    TaotaoResult register(TbUser user);
    TaotaoResult login(String username, String password);
    TaotaoResult getUserByToken(String token);
    TaotaoResult logout(String token);
}
