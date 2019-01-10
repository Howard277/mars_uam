package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.Role;
import com.mars.mars_uam.repository.ResourceRepository;
import com.mars.mars_uam.repository.RoleRepository;
import com.mars.mars_uam.repository.RoleResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


/**
 * 角色控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("role")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;

    /**
     * 角色列表
     *
     * @return
     */
    @GetMapping("roleList")
    public ModelAndView roleList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("role/roleList");
        mav.addObject("title", "角色列表");
        return mav;
    }

    /**
     * 添加角色
     *
     * @return
     */
    @GetMapping("addRole")
    public String addRole() {
        return "role/addRole";
    }

    /**
     * 修改角色
     *
     * @return
     */
    @GetMapping("modifyRole")
    public ModelAndView modifyRole(ModelAndView mav, @RequestParam String roleCode) {
        mav.setViewName("role/modifyRole");
        Role role = roleRepository.findByRoleCode(roleCode);
        mav.addObject("role", role);
        return mav;
    }
}
