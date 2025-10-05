package com.itheima.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author nijikelin
 * @Date 2025/10/3 16:11
 * @Description 用户没有权限访问
 */
@Slf4j
public class StockAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.warn("用户没有权限访问{}",e.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        R<Object> error = R.error(ResponseCode.NOT_PERMISSION);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
