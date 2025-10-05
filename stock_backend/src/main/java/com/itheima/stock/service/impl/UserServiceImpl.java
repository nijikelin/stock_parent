package com.itheima.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.stock.constant.StockConstant;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.service.UserService;
import com.itheima.stock.utils.IdWorker;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;
import com.itheima.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PermissionService permissionService;

    @Override
    public SysUser getUserByName(String userName) {
        return sysUserMapper.findByUserName(userName);
    }

    @Override
    public R<LoginRespVo> login(LoginReqVo loginReqVo) {
        //1.参数校验
        if(loginReqVo==null || StringUtils.isBlank(loginReqVo.getUsername()) || StringUtils.isBlank(loginReqVo.getPassword())){
            return R.error(ResponseCode.DATA_ERROR);
        }

        //1.1验证码和sessionid存在校验
        if(StringUtils.isBlank(loginReqVo.getCode())||StringUtils.isBlank(loginReqVo.getSessionId())){
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }
        //1.2根据sessionId获取缓存到redis中的校验码
        String redisCode = (String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX + loginReqVo.getSessionId());
        //1.3判断获取的验证码是否存在以及输入的校验码与redis缓存中的验证码是否相同
        if((StringUtils.isBlank(redisCode))||!(loginReqVo.getCode().equalsIgnoreCase(redisCode))){
            return R.error(ResponseCode.CHECK_CODE_ERROR);

        }
        //2.从数据库取出数据
        SysUser byUserName = sysUserMapper.findByUserName(loginReqVo.getUsername());
        if(byUserName==null){
            return R.error(ResponseCode.DATA_ERROR);
        }
        String password = byUserName.getPassword();
        //3.密码比对
        if (!passwordEncoder.matches(loginReqVo.getPassword(), password)) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        //获取权限列表
        List<SysPermission> permissions = permissionService.getPermissionByUserId(byUserName.getId());
        //组装返回对象
        LoginRespVo loginRespVo = new LoginRespVo();
        BeanUtils.copyProperties(byUserName,loginRespVo);
            //设置菜单数据
        loginRespVo.setMenus(permissionService.buildMenuTree(permissions));
            //设置权限数据
        loginRespVo.setPermissions(permissionService.extractButtonPermissions(permissions));

        return R.ok(loginRespVo);
    }

    @Override
    public R<Map> getCapchaCode() {
        //1、获取验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 50, 4, 2);
        captcha.setBackground(Color.lightGray);
        String code = captcha.getCode();
        String imageData = captcha.getImageBase64();

        //2、获取sessionid
        String sessionId = String.valueOf(idWorker.nextId());
        log.info("sessionId是{},校验码是{}",sessionId,code);

        //3、将sessionid作为key，校验码作为value保存在redis中
        redisTemplate.opsForValue().set(StockConstant.CHECK_PREFIX +sessionId,code,30,TimeUnit.MINUTES);
        HashMap<String,String> info = new HashMap<>();
        info.put("sessionId",sessionId);
        info.put("imageData",imageData);
        return R.ok(info);

    }
}
