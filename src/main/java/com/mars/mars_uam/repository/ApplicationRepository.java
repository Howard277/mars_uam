package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {
    /**
     * 通过系统编号查询
     *
     * @param appCode
     * @return
     */
    Application findByAppCode(String appCode);

    /**
     * 通过appCode集合，查找系统信息
     *
     * @param appCodeSet
     * @return
     */
    List<Application> findByAppCodeIn(Collection<String> appCodeSet);

    /**
     * 通过 应用系统编码、应用系统名称 模糊匹配分页数据
     *
     * @param appCode
     * @param appName
     * @param pageable
     * @return
     */
    Page<Application> findByAppCodeLikeOrAppNameLike(String appCode, String appName, Pageable pageable);
}
