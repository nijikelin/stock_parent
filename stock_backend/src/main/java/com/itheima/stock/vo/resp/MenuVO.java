package com.itheima.stock.vo.resp;

import lombok.Data;

import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/9/29 16:04
 * @Description 用户登录后看到的菜单
 */
@Data
public class MenuVO {
    private Long id;          // 权限ID
    private String title;       // 权限标题
    private String icon;        // 权限图标
    private String path;        // 请求地址
    private String name;        // 前端Vue组件名称
    private List<MenuVO> children; // 子菜单列表（非null）
}
