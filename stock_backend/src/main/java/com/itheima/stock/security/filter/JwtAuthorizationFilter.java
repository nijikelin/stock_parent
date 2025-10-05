package com.itheima.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.security.utils.JwtTokenUtil;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/10/2 17:31
 * @Description 自定义授权过滤器
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1.从request对象中获取token 约定key：Authorization
        String tokenStr = request.getHeader(StockConstant.TOKEN_HEADER);
        if (tokenStr==null) {
            filterChain.doFilter(request,response);
            return;
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        Claims claims = JwtTokenUtil.checkJWT(tokenStr);
        if (claims==null) {
            R<Object> error = R.error(ResponseCode.INVALID_TOKEN);
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }
        String username = JwtTokenUtil.getUsername(tokenStr);
        String userRole = JwtTokenUtil.getUserRole(tokenStr);
        String stripRoles = StringUtils.strip(userRole, "[]");
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stripRoles);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request,response);
    }
}
