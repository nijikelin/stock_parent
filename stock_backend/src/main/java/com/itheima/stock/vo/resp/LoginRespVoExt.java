package com.itheima.stock.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/10/2 13:11
 * @Description
 */
@Data
public class LoginRespVoExt {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;          // 用户ID
    private String username;    // 用户名
    private String phone;       // 手机号
    private String nickName;    // 昵称
    private String realName;    // 真实姓名
    private Integer sex;        // 性别
    private Integer status;     // 状态
    private String email;       // 邮箱
    private List<MenuVO> menus; // 侧边栏菜单树
    private List<String> permissions; // 按钮权限标识列表
    private String accessToken; //认证票据

}
