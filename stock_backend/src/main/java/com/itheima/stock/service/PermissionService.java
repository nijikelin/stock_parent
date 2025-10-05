package com.itheima.stock.service;


import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.vo.resp.MenuVO;

import java.util.List;

/**
 * @Author nijikelin
 * @Date 2025/9/29 16:43
 * @Description 权限服务类
 */
public interface PermissionService {
    /**
     * 获取用户权限列表
     *
     * @param userId
     * @return
     */
    List<SysPermission> getPermissionByUserId(Long userId);

    /**
     * 建立菜单树
     *
     * @param permissions
     * @return
     */
    List<MenuVO> buildMenuTree(List<SysPermission> permissions);

    /**
     * 提取按钮权限
     *
     * @param permissions
     * @return
     */
    List<String> extractButtonPermissions(List<SysPermission> permissions);
}
