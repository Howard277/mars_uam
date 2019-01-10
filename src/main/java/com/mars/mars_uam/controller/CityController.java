package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.City;
import com.mars.mars_uam.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 城市控制器
 * Created by wuketao on 2018/2/12.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("city")
public class CityController {

    @Autowired
    private CityRepository cityRepository;

    /**
     * 城市列表
     *
     * @return
     */
    @GetMapping("cityList")
    public ModelAndView cityList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("city/cityList");
        mav.addObject("title", "城市列表");
        return mav;
    }

    /**
     * 添加城市
     *
     * @return
     */
    @GetMapping("addCity")
    public String addCity() {
        return "city/addCity";
    }

    /**
     * 修改城市
     *
     * @return
     */
    @GetMapping("modifyCity")
    public ModelAndView modifyCity(ModelAndView mav, @RequestParam String cityCode) {
        mav.setViewName("city/modifyCity");
        City city = cityRepository.findByCityCode(cityCode);
        mav.addObject("city", city);
        return mav;
    }



}
