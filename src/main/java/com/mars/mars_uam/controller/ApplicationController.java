package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.Application;
import com.mars.mars_uam.repository.ApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 应用系统 控制器
 * Created by wuketao on 2018/2/11.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("app")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * 应用系统列表
     *
     * @return
     */
    @GetMapping("appList")
    public ModelAndView appList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("app/appList");
        mav.addObject("title", "应用程序列表");
        return mav;
    }

    /**
     * 添加系统
     *
     * @return
     */
    @GetMapping("addApp")
    public String addApp() {
        return "app/addApp";
    }

    /**
     * 修改 应用程序
     *
     * @param appCode
     * @return
     */
    @GetMapping("modifyApp")
    public ModelAndView modifyApp(@RequestParam String appCode) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("app/modifyApp");
        Application app = applicationRepository.findByAppCode(appCode);
        mav.addObject("app", app);
        return mav;
    }

}
