package com.itheima.stock.mapper;

import com.itheima.stock.pojo.entity.SysPermission;
import com.itheima.stock.pojo.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 1
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2025-09-04 11:48:51
* @Entity com.itheima.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findByUserName (@Param("name") String userName);

}
