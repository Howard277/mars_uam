package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.Resource;
import com.mars.mars_uam.entity.RoleResource;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.ResourceRepository;
import com.mars.mars_uam.repository.RoleResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;


/**
 * 资源控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@RestController
@RequestMapping("api/resource")
public class ApiResourceController2 {

    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;

    /**
     * 获取资源分页数据
     *
     * @return
     */
    @PostMapping("resourcePage")
    @ResponseBody
    public Page<Resource> resourcePage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                                       @RequestParam(required = false) String searchCondition) {
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return resourceRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return resourceRepository.findByResourceCodeLikeOrResourceNameLikeOrAppCodeLikeOrAppNameLike(searchCondition, searchCondition, searchCondition, searchCondition, pageable);
        }
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

    /**
     * 保存 资源
     *
     * @param resource
     * @return
     */
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    @PostMapping("saveResource")
    @ResponseBody
    public Map<String, Object> saveResource(@RequestBody Resource resource, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        //通过查询旧数据，判断是新增还是修改
        Resource oldResource = resourceRepository.findByResourceCode(resource.getResourceCode());
        if (null == oldResource) {
            resource.setCreateTime(new Date());
            resource.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        } else {
            resource.setCreateTime(oldResource.getCreateTime());
            resource.setCreator(oldResource.getCreator());
        }

        resource.setUpdateTime(new Date());
        resource.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());

        //组装目标资源-角色 关系集合
        List<RoleResource> targetRoleResourceList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(resource.getRoles())) {
            for (String key : JSONObject.parseObject(resource.getRoles()).keySet()) {
                RoleResource rr = new RoleResource();
                rr.setAppCode(resource.getAppCode());
                rr.setResourceCode(resource.getResourceCode());
                rr.setRoleCode(key);
                rr.setCreateTime(new Date());
                rr.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
                targetRoleResourceList.add(rr);
            }
        }

        //获取旧的资源-角色 关系
        List<RoleResource> oldRoleResourceList = new ArrayList<>();
        if (null != oldResource) {
            oldRoleResourceList = roleResourceRepository.findByResourceCodeAndAppCode(oldResource.getResourceCode(), oldResource.getAppCode());
        }

        //删除多余的资源-角色 关系
        List<RoleResource> deleteRoleResourceList = new ArrayList<>();
        for (RoleResource orr : oldRoleResourceList) {
            if (targetRoleResourceList.stream().filter(trr -> orr.getRoleCode().equals(trr.getRoleCode()) && orr.getAppCode().equals(trr.getAppCode())).count() == 0) {
                deleteRoleResourceList.add(orr);
            }
        }

        //新增的资源-角色 关系
        List<RoleResource> addRoleResourceList = new ArrayList<>();
        for (RoleResource trr : targetRoleResourceList) {
            if (oldRoleResourceList.stream().filter(orr -> orr.getRoleCode().equals(trr.getRoleCode()) && orr.getAppCode().equals(trr.getAppCode())).count() == 0) {
                addRoleResourceList.add(trr);
            }
        }

        //删除多余 关系
        for (RoleResource rr : deleteRoleResourceList) {
            roleResourceRepository.delete(rr);
        }
        //添加新 关系
        for (RoleResource rr : addRoleResourceList) {
            roleResourceRepository.save(rr);
        }

        //保存资源对象
        resourceRepository.save(resource);

        result.put("flag", true);

        return result;
    }

    /**
     * 检查 资源编码是否被占用
     *
     * @param resourceCode
     * @return
     */
    @GetMapping("checkResourceCode")
    @ResponseBody
    public JSONObject checkResourceCode(@RequestParam String resourceCode) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "编码已存在！");

        //从数据库中查找编码是否被使用
        Resource resource = resourceRepository.findByResourceCode(resourceCode);
        if (null == resource) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
