package com.itheima.stock.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.security.user.LoginUserDetail;
import com.itheima.stock.security.utils.JwtTokenUtil;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.LoginRespVoExt;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @Author nijikelin
 * @Date 2025/10/1 20:46
 * @Description 认证过滤器
 */
public class JwtLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private RedisTemplate redisTemplate;

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public JwtLoginAuthenticationFilter(String loginUrl) {
        super(loginUrl);
    }

    /**
     * 用户认证处理的方法
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //1.判断请求方法必须为POST提交，且提交的数据的内容必须是application/json格式的数据
        if (!request.getMethod().equals("POST") ||
                !(request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_UTF8_VALUE))) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //2.将请求数据反序列化为实体对象
        ServletInputStream in = request.getInputStream();
        LoginReqVo reqVo = new ObjectMapper().readValue(in, LoginReqVo.class);
        //设置响应格式和编码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        //3.判断参数是否合法
        if (reqVo == null || StringUtils.isBlank(reqVo.getUsername())
                || StringUtils.isBlank(reqVo.getPassword())
                || StringUtils.isBlank(reqVo.getSessionId()) || StringUtils.isBlank(reqVo.getCode())) {
            R<Object> resp = R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(resp));
            return null;
        }
        //4.校验验证码
        String rCheckCode = (String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + reqVo.getSessionId());
        if (rCheckCode == null || !rCheckCode.equalsIgnoreCase(reqVo.getCode())) {
            R<Object> resp = R.error(ResponseCode.CHECK_CODE_ERROR.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(resp));
            return null;
        }
        //5.将用户名密码封装在认证票据对象下
        String username = reqVo.getUsername();
        String password = reqVo.getPassword();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {

        LoginUserDetail principal = ((LoginUserDetail) authResult.getPrincipal());
        String username = principal.getUsername();
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        // 生成票据 ["P5","ROLE_ADMIN"]
        String tokenStr = JwtTokenUtil.createToken(username, authorities.toString());
        // 响应数据格式 json
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 编码格式
        response.setCharacterEncoding("UTF-8");
        // 构建响应实体对象
        LoginRespVoExt respVo = new LoginRespVoExt();
        BeanUtils.copyProperties(principal, respVo);
        respVo.setAccessToken(tokenStr);
        R<LoginRespVoExt> ok = R.ok(respVo);
        response.getWriter().write(new ObjectMapper().writeValueAsString(ok));

    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 编码格式
        response.setCharacterEncoding("UTF-8");
        // 构建响应实体对象

        R<Object> error = R.error(ResponseCode.ERROR);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));

    }
}