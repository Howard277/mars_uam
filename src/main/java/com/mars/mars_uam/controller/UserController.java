package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.UserRepository;
import com.mars.mars_uam.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户信息控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    /**
     * 系统默认的重置密码
     */
    @Value("${defaultPassword}")
    private String defaultPassword;

    /**
     * 用户列表
     *
     * @return
     */
    @GetMapping("userList")
    public ModelAndView userList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/userList");
        mav.addObject("title", "用户列表");
        return mav;
    }

    /**
     * 添加人员
     *
     * @return
     */
    @GetMapping("addUser")
    public String addUser() {
        return "user/addUser";
    }

    /**
     * 修改人员
     *
     * @return
     */
    @GetMapping("modifyUser")
    public ModelAndView modifyUser(ModelAndView mav, @RequestParam String loginName) {
        mav.setViewName("user/modifyUser");
        User user = userRepository.findByLoginName(loginName);
        mav.addObject("user", user);
        return mav;
    }

    /**
     * 显示用户信息
     *
     * @return
     */
    @GetMapping("showUserInfo")
    public ModelAndView showUserInfo() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/showUserInfo");
        return mav;
    }

    /**
     * 修改密码
     *
     * @return
     */
    @GetMapping("changePassword")
    public String changePassword() {
        return "user/changePassword";
    }

}
