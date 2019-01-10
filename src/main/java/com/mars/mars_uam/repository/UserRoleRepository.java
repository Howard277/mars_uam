package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

    /**
     * 通过登录名查找
     *
     * @param loginName
     * @return
     */
    List<UserRole> findByLoginName(String loginName);

    /**
     * 通过登录名删除
     *
     * @param loginName
     * @return
     */
    Integer deleteByLoginName(String loginName);

    /**
     * 根据应用系统编码删除用户-角色信息
     *
     * @param appCode
     * @return
     */
    Integer deleteByAppCode(String appCode);

}
