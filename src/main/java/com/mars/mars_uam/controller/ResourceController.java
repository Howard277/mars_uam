package com.mars.mars_uam.controller;

import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.entity.Resource;
import com.mars.mars_uam.repository.ResourceRepository;
import com.mars.mars_uam.repository.RoleResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.*;


/**
 * 资源控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping("resource")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;

    /**
     * 资源列表
     *
     * @return
     */
    @GetMapping("resourceList")
    public ModelAndView resourceList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("resource/resourceList");
        mav.addObject("title", "资源列表");
        return mav;
    }

    /**
     * 添加资源
     *
     * @return
     */
    @GetMapping("addResource")
    public String addResource() {
        return "resource/addResource";
    }

    /**
     * 修改资源
     *
     * @return
     */
    @GetMapping("modifyResource")
    public ModelAndView modifyResource(ModelAndView mav, @RequestParam String resourceCode) {
        mav.setViewName("resource/modifyResource");
        Resource resource = resourceRepository.findByResourceCode(resourceCode);
        mav.addObject("resource", resource);
        return mav;
    }

    /**
     * 删除资源
     *
     * @param resourceCode
     * @return
     */
    @PostMapping("deleteResource")
    @ResponseBody
    public Map<String, Object> deleteResource(@RequestParam String resourceCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            Resource resource = new Resource();
            resource.setResourceCode(resourceCode);
            resourceRepository.delete(resourceCode);
            roleResourceRepository.deleteByResourceCode(resourceCode);
            map.put("flag", true);
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

}
