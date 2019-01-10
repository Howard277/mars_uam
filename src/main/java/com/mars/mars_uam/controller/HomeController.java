package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.Application;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.entity.UserRole;
import com.mars.mars_uam.repository.ApplicationRepository;
import com.mars.mars_uam.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 主页控制器
 * Created by wuketao on 2018/3/15.
 */
@SystemLogAnnotation
@Slf4j
@RequestMapping("home")
@Controller
public class HomeController {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * 主页
     *
     * @return
     */
    @RequestMapping(path = {"", "index"})
    public ModelAndView index(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("home/index");

        mav.addObject("title","系统清单");

        List<Application> appList = new ArrayList<>();
        if (null != session.getAttribute(AllConstant.USERKEY)) {
            List<UserRole> userRoleList = userRoleRepository.findByLoginName(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
            Set<String> appCodeSet = userRoleList.stream().map(ur -> ur.getAppCode()).collect(Collectors.toSet());
            appList = applicationRepository.findByAppCodeIn(appCodeSet);
        }
        mav.addObject("appList", appList);
        return mav;
    }
}
