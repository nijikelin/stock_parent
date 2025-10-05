package com.itheima.stock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author nijikelin
 * @Date 2025/10/3 16:05
 * @Description 匿名用户拒绝访问
 */
@Slf4j
public class StockAccessDenyHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.warn("匿名用户拒绝访问{}",e.getMessage());
        R<Object> error = R.error(ResponseCode.ANONMOUSE_NOT_PERMISSION);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
