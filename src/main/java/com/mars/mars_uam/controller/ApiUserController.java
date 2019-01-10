package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.common.HBEncrypt;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.entity.UserRole;
import com.mars.mars_uam.repository.UserRepository;
import com.mars.mars_uam.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;


/**
 * 用户信息控制器
 * Created by wuketao on 2018/1/25.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@RestController
@RequestMapping("api/user")
public class ApiUserController {

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
     * 获取人员分页数据
     *
     * @return
     */
    @PostMapping("userPage")
    @ResponseBody
    public Page<User> userPage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                               @RequestParam(required = false) String searchCondition) {
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return userRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return userRepository.findByLoginNameLikeOrUserNameLikeOrOrgCodeLikeOrOrgNameLike(searchCondition, searchCondition, searchCondition, searchCondition, pageable);
        }
    }

    /**
     * 删除人员
     *
     * @param loginName
     * @return
     */
    @PostMapping("deleteUser")
    @ResponseBody
    public Map<String, Object> deleteUser(@RequestParam String loginName) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            User user = new User();
            user.setLoginName(loginName);
            userRepository.delete(user);
            userRoleRepository.deleteByLoginName(user.getLoginName());
            map.put("flag", true);
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

    /**
     * 保存 人员信息
     *
     * @param user
     * @return
     */
    @PostMapping("saveUser")
    @ResponseBody
    public Map<String, Object> saveUser(@RequestBody User user, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        User oldUser = userRepository.findByLoginName(user.getLoginName());
        if (null == oldUser) {
            //添加新用户
            user.setCreateTime(new Date());
            user.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
            try {
                String enPassword = HBEncrypt.shaDes(user.getPassword());
                user.setPassword(enPassword);
            } catch (Exception ex) {
                log.error("{}", ex);
            }
        } else {
            //修改用户
            user.setCreateTime(oldUser.getCreateTime());
            user.setCreator(oldUser.getCreator());
            user.setPassword(oldUser.getPassword());
        }

        //设置更新信息
        user.setUpdateTime(new Date());
        user.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());

        //组装目标用户-角色 关系集合
        List<UserRole> targetUserRoleList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(user.getRoles())) {
            JSONArray roleArray = JSONArray.parseArray(user.getRoles());
            for (int i = 0; i < roleArray.size(); i++) {
                JSONObject obj = roleArray.getJSONObject(i);
                UserRole ur = new UserRole();
                ur.setAppCode(obj.getString("appCode"));
                ur.setLoginName(user.getLoginName());
                ur.setRoleCode(obj.getString("roleCode"));
                ur.setCreateTime(new Date());
                ur.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
                targetUserRoleList.add(ur);
            }
        }

        //获取旧的用户-角色 关系
        List<UserRole> oldUserRoleList = new ArrayList<>();
        if (null != oldUser) {
            oldUserRoleList = userRoleRepository.findByLoginName(user.getLoginName());
        }

        //删除多余的用户-角色 关系
        List<UserRole> deleteUserRoleList = new ArrayList<>();
        for (UserRole our : oldUserRoleList) {
            if (targetUserRoleList.stream().filter(tur -> our.getRoleCode().equals(tur.getRoleCode()) && our.getAppCode().equals(tur.getAppCode())).count() == 0) {
                deleteUserRoleList.add(our);
            }
        }

        //新增的用户-角色 关系
        List<UserRole> addUserRoleList = new ArrayList<>();
        for (UserRole tur : targetUserRoleList) {
            if (oldUserRoleList.stream().filter(our -> our.getRoleCode().equals(tur.getRoleCode()) && our.getAppCode().equals(tur.getAppCode())).count() == 0) {
                addUserRoleList.add(tur);
            }
        }

        //删除多余 关系
        for (UserRole ur : deleteUserRoleList) {
            userRoleRepository.delete(ur);
        }
        //添加新 关系
        for (UserRole ur : addUserRoleList) {
            userRoleRepository.save(ur);
        }

        userRepository.save(user);
        result.put("flag", true);

        return result;
    }

    /**
     * 重置密码
     *
     * @param loginName
     * @return
     */
    @PostMapping("resetPassword")
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam String loginName) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        map.put("msg", "重置失败！");

        if (!Strings.isNullOrEmpty(loginName)) {
            try {
                String enPassword = HBEncrypt.shaDes(defaultPassword);
                User user = userRepository.findByLoginName(loginName);
                if (null != user) {
                    user.setPassword(enPassword);
                    userRepository.save(user);
                    map.put("flag", true);
                } else {
                    map.put("msg", "用户不存在！");
                }
            } catch (Exception ex) {
                log.error("{}", ex);
            }
        } else {
            map.put("msg", "登录名不能为空！");
        }

        return map;
    }

    /**
     * 保存密码
     *
     * @return
     */
    @PostMapping("savePassword")
    @ResponseBody
    public Map<String, Object> savePassword(@RequestParam String loginName, @RequestParam String oldPassword, @RequestParam String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        map.put("msg", "保存失败！");

        //通过登录名和密码查询用户
        String enPassword = "";
        try {
            enPassword = HBEncrypt.shaDes(oldPassword);
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        User user = userRepository.findByLoginNameAndPassword(loginName, enPassword);
        if (null == user) {
            map.put("msg", "密码错误！");
        } else {
            try {
                String newenPassword = HBEncrypt.shaDes(password);
                user.setPassword(newenPassword);
                userRepository.save(user);
                map.put("flag", true);
            } catch (Exception ex) {
                log.error("{}", ex);
            }
        }
        return map;
    }

    /**
     * 检查 登录名是否被占用
     *
     * @param loginName
     * @return
     */
    @GetMapping("checkLoginName")
    @ResponseBody
    public JSONObject checkLoginName(@RequestParam String loginName) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "用户名已存在！");

        //从数据库中查找登录名是否被使用
        User user = userRepository.findByLoginName(loginName);
        if (null == user) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
