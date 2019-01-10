package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.Organization;
import com.mars.mars_uam.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 组织机构信息
 * Created by wuketao on 2018/2/12.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("org")
public class OrganizationController {
    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * 组织机构列表
     *
     * @return
     */
    @GetMapping("orgList")
    public ModelAndView orgList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("organization/orgList");
        mav.addObject("title", "组织机构列表");
        return mav;
    }

    /**
     * 添加组织机构
     *
     * @return
     */
    @GetMapping("addOrg")
    public String addOrg() {
        return "organization/addOrg";
    }


    /**
     * 修改组织机构
     *
     * @return
     */
    @GetMapping("modifyOrg")
    public ModelAndView modifyOrg(ModelAndView mav, @RequestParam String orgCode) {
        mav.setViewName("organization/modifyOrg");
        Organization org = organizationRepository.findByOrgCode(orgCode);
        mav.addObject("org", org);
        return mav;
    }
}
