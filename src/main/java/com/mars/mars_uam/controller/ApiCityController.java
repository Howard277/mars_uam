package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.City;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 城市控制器
 * Created by wuketao on 2018/2/12.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@RestController
@RequestMapping("api/city")
public class ApiCityController {

    @Autowired
    private CityRepository cityRepository;

    /**
     * 获取城市列表
     *
     * @return
     */
    @GetMapping("findAll")
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    /**
     * 获取城市分页数据
     *
     * @return
     */
    @PostMapping("cityPage")
    public Page<City> cityPage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize, @RequestParam(required = false) String searchCondition) {
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return cityRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return cityRepository.findByCityCodeLikeOrCityNameLike(searchCondition, searchCondition, pageable);
        }
    }

    /**
     * 删除城市
     *
     * @param cityCode
     * @return
     */
    @PostMapping("deleteCity")
    public Map<String, Object> deleteCity(@RequestParam String cityCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            City city = new City();
            city.setCityCode(cityCode);
            cityRepository.delete(city);
            map.put("flag", true);
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

    /**
     * 保存 城市信息
     *
     * @param city 城市信息
     * @return
     */
    @PostMapping("saveCity")
    public Map<String, Object> saveCity(@RequestBody City city, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        //通过城市编码查找
        City oldCity = cityRepository.findByCityCode(city.getCityCode());
        if (null == oldCity) {
            //添加创建人和创建时间
            city.setCreateTime(new Date());
            city.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        } else {
            city.setCreator(oldCity.getCreator());
            city.setCreateTime(oldCity.getCreateTime());
        }

        //添加创建人和创建时间
        city.setUpdateTime(new Date());
        city.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());

        //保存到数据库
        cityRepository.save(city);
        result.put("flag", true);

        return result;
    }

    /**
     * 获取城市列表
     *
     * @param cityCode
     * @param cityName
     * @return
     */
    @GetMapping("getCity")
    public List<City> getCity(@RequestParam(required = false) String cityCode, @RequestParam(required = false) String cityName) {
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching(); //构建对象

        City city = new City();
        if (!Strings.isNullOrEmpty(cityCode)) {
            matcher.withMatcher("cityCode", ExampleMatcher.GenericPropertyMatchers.caseSensitive());
            city.setCityCode(cityCode);
        }
        if (!Strings.isNullOrEmpty(cityName)) {
            matcher.withMatcher("cityName", ExampleMatcher.GenericPropertyMatchers.caseSensitive());
            city.setCityName(cityName);
        }

        //创建实例
        Example<City> ex = Example.of(city, matcher);

        return cityRepository.findAll(ex);
    }

    /**
     * 检查 城市编码是否被占用
     *
     * @param cityCode
     * @return
     */
    @GetMapping("checkCityCode")
    public JSONObject checkCityCode(@RequestParam String cityCode) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "编码已存在！");

        //从数据库中查找编码是否被使用
        City city = cityRepository.findByCityCode(cityCode);
        if (null == city) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
