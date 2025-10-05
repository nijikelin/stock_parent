package com.itheima.stock.service.impl;

import com.itheima.stock.mapper.SysPermissionMapper;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.vo.resp.MenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/9/29 16:50
 * @Description
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysPermission> getPermissionByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<SysPermission> permissions = sysPermissionMapper.getPermissionByUserId(userId);
        return permissions == null ? Collections.emptyList() : permissions;
    }

    @Override
    public List<MenuVO> buildMenuTree(List<SysPermission> permissions) {
        List<SysPermission> menuPermissions = permissions.stream().filter(p -> (p.getType() == 1) || p.getType() == 2).collect(Collectors.toList());

        List<MenuVO> rootMenus = new ArrayList<>();
        for (SysPermission permission : menuPermissions) {
            if (0 == (permission.getPid())) {
                MenuVO rootMenu = convertToMenuVO(permission);
                rootMenu.setChildren(findChildren(permission.getId(), menuPermissions));
                rootMenus.add(rootMenu);
            }
        }
        return rootMenus;
    }

    private List<MenuVO> findChildren(Long parentId, List<SysPermission> allMenuPermissions) {
        List<MenuVO> children = new ArrayList<>();
        for (SysPermission permission : allMenuPermissions) {
            Long pid = permission.getPid();
            if (parentId.equals(pid)) {
                MenuVO childMenu = convertToMenuVO(permission);
                childMenu.setChildren(findChildren(permission.getId(), allMenuPermissions));
                children.add(childMenu);
            }
        }
        return children.isEmpty() ? Collections.emptyList() : children;
    }

    private MenuVO convertToMenuVO(SysPermission permission) {
        MenuVO menuVO = new MenuVO();
        menuVO.setId(permission.getId());
        menuVO.setTitle(permission.getTitle());
        menuVO.setIcon(permission.getIcon());
        menuVO.setPath(permission.getUrl());
        menuVO.setName(permission.getName());
        menuVO.setChildren(new ArrayList<>());
        return menuVO;
    }

    @Override
    public List<String> extractButtonPermissions(List<SysPermission> permissions) {
        if (permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .filter(p -> p.getType() == 3)
                .map(SysPermission::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
