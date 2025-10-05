package com.itheima.stock.vo.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author by itheima
 * @Date 2021/12/24
 * @Description 登录后响应前端的vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRespVo {
    /**
     * 用户ID
     * 将Long类型数字进行json格式转化时，转成String格式类型
     */
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

}