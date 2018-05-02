package com.taotao.controller;


import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;
import com.taotao.service.UserService;
import com.taotao.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public TaotaoResult checkUserData(@PathVariable String param, @PathVariable int type){
        return userService.checkData(param, type);
    }

    @RequestMapping("/user/register")
    @ResponseBody
    public TaotaoResult register(TbUser tbUser){
        return userService.register(tbUser);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(String username, String password, HttpServletRequest request, HttpServletResponse response){
        TaotaoResult result = userService.login(username,password);
        if (result.getStatus() == 200) {
            String token = result.getData().toString();
            CookieUtils.setCookie(request, response, "TOKEN_KEY", token);
        }
        return result;
    }

    @RequestMapping(value = "/user/token/{token}",
            //镇定返回的contentType
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Object getUserByToken(@PathVariable String token,String callback, HttpServletRequest request, HttpServletResponse response){
        TaotaoResult result = userService.getUserByToken(token);
        if (result.getStatus() == 200){
            CookieUtils.setCookie(request, response,"TOKEN_KEY", token);
        }
        return result;
    }

    @RequestMapping("/user/logout/{token}")
    @ResponseBody
    public TaotaoResult logout(@PathVariable String token, HttpServletRequest request, HttpServletResponse response){
        TaotaoResult result = userService.logout(token);
        if (result.getStatus() == 200){
            CookieUtils.deleteCookie(request, response, "TOKEN_KEY");
        }
        return userService.logout(token);
    }
}
