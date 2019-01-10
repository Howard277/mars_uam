package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.Resource;
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
public interface ResourceRepository extends JpaRepository<Resource, String>, JpaSpecificationExecutor<Resource> {

    /**
     * 通过资源编码查找资源
     *
     * @param resourceCode
     * @return
     */
    Resource findByResourceCode(String resourceCode);

    /**
     * 通过资源编码列表查询资源
     *
     * @param resourceCodeList
     * @return
     */
    List<Resource> findByResourceCodeInOrderBySortNo(List<String> resourceCodeList);

    /**
     * 通过系统编码获得资源
     *
     * @param appCode
     * @return
     */
    List<Resource> findByAppCodeOrderBySortNo(String appCode);

    /**
     * 根据系统编码删除资源
     *
     * @param appCode
     * @return
     */
    Integer deleteByAppCode(String appCode);

    /**
     * 通过 资源编码、资源名称、系统编码、系统名称 模糊匹配分页数据
     *
     * @param resourceCode
     * @param resourceName
     * @param appCode
     * @param appName
     * @param pageable
     * @return
     */
    Page<Resource> findByResourceCodeLikeOrResourceNameLikeOrAppCodeLikeOrAppNameLike(String resourceCode, String resourceName, String appCode, String appName, Pageable pageable);
}
