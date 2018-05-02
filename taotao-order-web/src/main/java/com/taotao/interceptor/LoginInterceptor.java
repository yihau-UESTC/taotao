package com.taotao.interceptor;

import com.taotao.pojo.TaotaoResult;
import com.taotao.service.UserService;
import com.taotao.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object o) throws Exception {
        String token = CookieUtils.getCookieValue(req, "TOKEN_KEY");
        if (StringUtils.isBlank(token)){
            String url = req.getRequestURL().toString();
            resp.sendRedirect("http://localhost:8089/page/login?redirectUrl="+ url);
            return false;
        }
        TaotaoResult result = userService.getUserByToken(token);
        if (result.getStatus() != 200){
            String url = req.getRequestURL().toString();
            resp.sendRedirect("http://localhost:8089/page/login?redirectUrl="+ url);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
