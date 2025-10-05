package com.itheima.stock.service;

import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;

import java.util.Map;

public interface UserService {
    SysUser getUserByName(String userName);

    R<LoginRespVo> login(LoginReqVo loginReqVo);

    R<Map> getCapchaCode();
}
