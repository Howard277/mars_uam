package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.RoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface RoleResourceRepository extends JpaRepository<RoleResource, Long>, JpaSpecificationExecutor<RoleResource> {

    /**
     * 通过角色编码 系统编码 查询
     *
     * @param roleCode
     * @return
     */
    List<RoleResource> findByRoleCodeAndAppCode(String roleCode, String appCode);

    /**
     * 通过资源编码 系统编码 查询
     *
     * @param resourceCode
     * @return
     */
    List<RoleResource> findByResourceCodeAndAppCode(String resourceCode, String appCode);

    /**
     * 通过资源编码 删除
     *
     * @param resourceCode
     * @return
     */
    Integer deleteByResourceCode(String resourceCode);

    /**
     * 通过角色编码列表查询数据
     *
     * @param roleCodeList
     * @return
     */
    List<RoleResource> findByRoleCodeIn(List<String> roleCodeList);

    /**
     * 根据系统编码删除 角色-资源 信息
     *
     * @param appCode
     * @return
     */
    Integer deleteByAppCode(String appCode);

    /**
     * 通过角色编码删除 角色-资源 信息
     *
     * @param roleCode
     * @return
     */
    Integer deleteByRoleCode(String roleCode);


}
