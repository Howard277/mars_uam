package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.Application;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用系统 控制器
 * Created by wuketao on 2018/2/11.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@RestController
@RequestMapping("api/app")
public class ApiApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;


    /**
     * 获取应用系统分页数据
     *
     * @return
     */
    @PostMapping("appPage")
    @ResponseBody
    public Page<Application> appPage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                                     @RequestParam(required = false) String searchCondition) {
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return applicationRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return applicationRepository.findByAppCodeLikeOrAppNameLike(searchCondition, searchCondition, pageable);
        }
    }

    /**
     * 保存 应用系统信息
     *
     * @param appCode
     * @param appName
     * @param description
     * @return
     */
    @PostMapping("saveApp")
    @ResponseBody
    public Map<String, Object> saveApp(@RequestParam String appCode, @RequestParam String appName, @RequestParam String url, @RequestParam String description, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        Application application = new Application();
        Application oldApp = applicationRepository.findByAppCode(appCode);
        if (null == oldApp) {
            //添加
            application.setAppCode(appCode);
            application.setCreateTime(new Date());
            application.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        } else {
            //修改
            application.setAppCode(appCode);
            application.setCreateTime(oldApp.getCreateTime());
            application.setCreator(oldApp.getCreator());
        }

        application.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        application.setUpdateTime(new Date());
        application.setAppName(appName);
        application.setUrl(url);
        application.setDescription(description);
        applicationRepository.save(application);
        result.put("flag", true);

        return result;
    }

    /**
     * 删除 系统
     *
     * @param appCode
     */
    @PostMapping("deleteApp")
    @ResponseBody
    public Map<String, Object> deleteApp(@RequestParam String appCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            if (!Strings.isNullOrEmpty(appCode)) {
                Application app = new Application();
                app.setAppCode(appCode);
                //删除系统
                applicationRepository.delete(app);
                //删除系统的角色
                roleRepository.deleteByAppCode(appCode);
                //删除系统的资源
                resourceRepository.deleteByAppCode(appCode);
                //根据系统编码，删除用户角色关系
                userRoleRepository.deleteByAppCode(appCode);
                //根据系统编码，删除角色资源关系
                roleResourceRepository.deleteByAppCode(appCode);
                map.put("flag", true);
            }
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

    /**
     * 检查 应用编码是否被占用
     *
     * @param appCode
     * @return
     */
    @GetMapping("checkAppCode")
    @ResponseBody
    public JSONObject checkAppCode(@RequestParam String appCode) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "编码已存在！");

        //从数据库中查找编码是否被使用
        Application app = applicationRepository.findByAppCode(appCode);
        if (null == app) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
