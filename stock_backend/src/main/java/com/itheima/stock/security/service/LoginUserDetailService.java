package com.itheima.stock.security.service;

import com.itheima.stock.mapper.SysRoleMapper;
import com.itheima.stock.mapper.SysUserMapper;
import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.pojo.entity.SysRole;
import com.itheima.stock.pojo.entity.SysUser;
import com.itheima.stock.security.user.LoginUserDetail;
import com.itheima.stock.service.PermissionService;
import com.itheima.stock.vo.resp.LoginRespVo;
import com.itheima.stock.vo.resp.MenuVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author nijikelin
 * @Date 2025/10/1 21:26
 * @Description 定义获取用户详情服务Bean
 */
@Component
public class LoginUserDetailService implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 根据传入的用户名获取用户信息：用户名 密文密码 权限信息
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser dbUser = sysUserMapper.findByUserName(username);
        if(dbUser == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        //获取权限列表，角色列表
        List<SysPermission> permissions = permissionService.getPermissionByUserId(dbUser.getId());
        List<SysRole> roles= sysRoleMapper.getRoleByUserId(dbUser.getId());

        //获取菜单数据
        List<MenuVO> menuVOS = permissionService.buildMenuTree(permissions);
        //获取按钮权限数据
        List<String> btnPers = permissionService.extractButtonPermissions(permissions);
        //获取spring security权限标识
        ArrayList<String> ps = new ArrayList<>();
        List<String> perms = permissions.stream()
                .filter(p->StringUtils.isNotBlank(p.getPerms()))
                .map(p->p.getPerms())
                .collect(Collectors.toList());
        ps.addAll(perms);
        List<String> rs = roles.stream().map(r -> "ROLE_" + r.getName()).collect(Collectors.toList());
        ps.addAll(rs);
        String[] psArray = ps.toArray(new String[ps.size()]);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(psArray);
        LoginUserDetail loginUserDetail = new LoginUserDetail();
        BeanUtils.copyProperties(dbUser,loginUserDetail);
        loginUserDetail.setMenus(menuVOS);
        loginUserDetail.setPermissions(btnPers);
        loginUserDetail.setAuthorities(authorityList);
        return loginUserDetail;
    }
}

