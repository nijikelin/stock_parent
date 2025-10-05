package com.itheima.stock.controller;

import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.service.UserService;
import com.itheima.stock.vo.req.LoginReqVo;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userName}")
    public SysUser getUserByName(@PathVariable("userName") String userName){
        return userService.getUserByName(userName);
    }
//    @PostMapping("/login")
//    public R<LoginRespVo> login(@RequestBody LoginReqVo loginReqVo){
//        R<LoginRespVo> r = userService.login(loginReqVo);
//        return r;
//    }
    @GetMapping("/captcha")
    public R<Map> getCaptchaCode(){
        return userService.getCapchaCode();
    }
}
