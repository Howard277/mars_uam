package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    /**
     * 通过角色编码查找角色
     *
     * @param roleCode
     * @return
     */
    Role findByRoleCode(String roleCode);

    /**
     * 通过系统编码查找角色集合
     *
     * @param appCode
     * @return
     */
    List<Role> findByAppCodeOrderByRoleCode(String appCode);

    /**
     * 查找指定角色编码的角色
     *
     * @param roleCodeList
     * @return
     */
    List<Role> findByRoleCodeInOrderByRoleCode(List<String> roleCodeList);

    /**
     * 根据应用系统编码 删除角色
     *
     * @param appCode
     * @return
     */
    Integer deleteByAppCode(String appCode);

    /**
     * 根据 角色编码、角色名称、系统编码、系统名称 模糊查询分页数据
     *
     * @param roleCode
     * @param roleName
     * @param appCode
     * @param appName
     * @param pageable
     * @return
     */
    Page<Role> findByRoleCodeLikeOrRoleNameLikeOrAppCodeLikeOrAppNameLike(String roleCode, String roleName, String appCode, String appName, Pageable pageable);
}
