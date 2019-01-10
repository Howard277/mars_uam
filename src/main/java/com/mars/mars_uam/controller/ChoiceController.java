package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.Resource;
import com.mars.mars_uam.entity.Role;
import com.mars.mars_uam.entity.RoleResource;
import com.mars.mars_uam.repository.ResourceRepository;
import com.mars.mars_uam.repository.RoleRepository;
import com.mars.mars_uam.repository.RoleResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wuketao on 2018/3/14.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("choice")
public class ChoiceController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * 选择城市
     *
     * @return
     */
    @GetMapping("choiceCity")
    public String choiceCity() {
        return "choice/choiceCity";
    }

    /**
     * 选择来源城市
     *
     * @return
     */
    @GetMapping("choiceSourceCity")
    public String choiceSourceCity() {
        return "choice/choiceSourceCity";
    }

    /**
     * 选择机构
     *
     * @return
     */
    @GetMapping("choiceOrg")
    public String choiceOrg() {
        return "choice/choiceOrg";
    }

    /**
     * 选择系统
     *
     * @return
     */
    @GetMapping("choiceApp")
    public String choiceApp() {
        return "choice/choiceApp";
    }

    /**
     * 选择角色
     *
     * @return
     */
    @GetMapping("choiceRole")
    public String choiceRole() {
        return "choice/choiceRole";
    }

    /**
     * 为资源选择角色
     *
     * @param appCode
     * @return
     */
    @GetMapping("choiceRolesForResource")
    public ModelAndView choiceRolesForResource(@RequestParam String appCode, @RequestParam String roles) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("choice/choiceRolesForResource");

        //获取该系统下的所有角色
        List<Role> roleList = roleRepository.findByAppCodeOrderByRoleCode(appCode);
        mav.addObject("roleList", roleList);

        //获取该资源已分配的所有角色
        mav.addObject("roles", roles);

        return mav;
    }

    /**
     * 为用户选择角色
     *
     * @param roles
     * @return
     */
    @GetMapping("choiceRolesForUser")
    public ModelAndView choiceRolesForUser(@RequestParam String roles) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("choice/choiceRolesForUser");

        //获取所有系统下的所有角色，按照 系统编码 角色编码 排序
        Sort sort = new Sort(Sort.Direction.ASC, "appCode", "roleCode");
        List<RoleResource> roleResourceList = roleResourceRepository.findAll();
        List<Resource> resourceList = resourceRepository.findAll();
        List<Role> roleList = roleRepository.findAll(sort);
        for (Role role : roleList) {
            role.setDescription("");
            List<RoleResource> tempRoleResourceList = roleResourceList.stream().filter(rr -> rr.getRoleCode().equals(role.getRoleCode())).collect(Collectors.toList());
            if (!tempRoleResourceList.isEmpty()) {
                List<List<Resource>> tempResourceList = tempRoleResourceList.stream().map(rr -> resourceList.stream().filter(r -> r.getResourceCode().equals(rr.getResourceCode())).collect(Collectors.toList())).collect(Collectors.toList());
                for (List<Resource> tempRList : tempResourceList) {
                    if (!tempRList.isEmpty()) {
                        role.setDescription(role.getDescription() + " " + tempRList.get(0).getResourceName());
                    }
                }
            }
        }
        mav.addObject("roleList", roleList);

        //获取该资源已分配的所有角色
        mav.addObject("roles", roles);

        return mav;
    }

    /**
     * 选择资源
     *
     * @return
     */
    @GetMapping("choiceResource")
    public String choiceResource() {
        return "choice/choiceResource";
    }
}
