package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface CityRepository extends JpaRepository<City, String>, JpaSpecificationExecutor<City>, QueryByExampleExecutor<City> {
    /**
     * 通过城市编码查找城市
     *
     * @param cityCode
     * @return
     */
    City findByCityCode(String cityCode);

    /**
     * 根据城市编码、城市名称 模糊查询分页数据
     * @param cityCode
     * @param cityName
     * @param pageable
     * @return
     */
    Page<City> findByCityCodeLikeOrCityNameLike(String cityCode, String cityName, Pageable pageable);

}
