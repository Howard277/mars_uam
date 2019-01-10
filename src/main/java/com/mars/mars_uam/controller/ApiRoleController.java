package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.Role;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.ResourceRepository;
import com.mars.mars_uam.repository.RoleRepository;
import com.mars.mars_uam.repository.RoleResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;


/**
 * 角色控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@Controller
@RequestMapping("api/role")
public class ApiRoleController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleResourceRepository roleResourceRepository;

    /**
     * 获取角色分页数据
     *
     * @return
     */
    @PostMapping("rolePage")
    @ResponseBody
    public Page<Role> rolePage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                               @RequestParam(required = false) String searchCondition) {
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return roleRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return roleRepository.findByRoleCodeLikeOrRoleNameLikeOrAppCodeLikeOrAppNameLike(searchCondition, searchCondition, searchCondition, searchCondition, pageable);
        }
    }

    /**
     * 删除角色
     *
     * @param roleCode
     * @return
     */
    @PostMapping("deleteRole")
    @ResponseBody
    public Map<String, Object> deleteRole(@RequestParam String roleCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            Role role = new Role();
            role.setRoleCode(roleCode);
            roleRepository.delete(role);
            roleResourceRepository.deleteByRoleCode(roleCode);
            map.put("flag", true);
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

    /**
     * 保存 角色
     *
     * @param role
     * @return
     */
    @PostMapping("saveRole")
    @ResponseBody
    public Map<String, Object> saveRole(@RequestBody Role role, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        //通过查找旧记录是否存在，判断是新增还是修改。
        Role oldRole = roleRepository.findByRoleCode(role.getRoleCode());
        if (null == oldRole) {
            role.setCreateTime(new Date());
            role.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        } else {
            role.setCreateTime(oldRole.getCreateTime());
            role.setCreator(oldRole.getCreator());
        }

        //设置更新信息
        role.setUpdateTime(new Date());
        role.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        roleRepository.save(role);
        result.put("flag", true);

        return result;
    }

    /**
     * 检查 角色编码是否被占用
     *
     * @param roleCode
     * @return
     */
    @GetMapping("checkRoleCode")
    @ResponseBody
    public JSONObject checkRoleCode(@RequestParam String roleCode) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "编码已存在！");

        //从数据库中查找编码是否被使用
        Role role = roleRepository.findByRoleCode(roleCode);
        if (null == role) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
