package com.mars.mars_uam.repository;

import com.mars.mars_uam.constant.EnableEnum;
import com.mars.mars_uam.entity.Organization;
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
public interface OrganizationRepository extends JpaRepository<Organization, String>, JpaSpecificationExecutor<Organization> {
    /**
     * 通过组织机构编码查找
     *
     * @param orgCode
     * @return
     */
    Organization findByOrgCode(String orgCode);

    /**
     * 通过状态获取组织机构
     *
     * @param enable
     * @return
     */
    List<Organization> findByEnable(EnableEnum enable);

    /**
     * 通过 组织机构编码、组织机构名称、父级组织机构编码、父级组织机构名称、城市编码、城市名称 模糊搜索分页查询
     * @param orgCode
     * @param orgName
     * @param parentOrgCode
     * @param parentOrgName
     * @param cityCode
     * @param cityName
     * @param pageable
     * @return
     */
    Page<Organization> findByOrgCodeLikeOrOrgNameLikeOrParentOrgCodeLikeOrParentOrgNameLikeOrCityCodeLikeOrCityNameLike(
            String orgCode, String orgName, String parentOrgCode, String parentOrgName, String cityCode, String cityName, Pageable pageable
    );
}
