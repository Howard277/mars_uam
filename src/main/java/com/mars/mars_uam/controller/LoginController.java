package com.mars.mars_uam.controller;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.common.HBEncrypt;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.entity.*;
import com.mars.mars_uam.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录控制器
 *
 * @author wuketao
 */
@SystemLogAnnotation
@Slf4j
@Controller
@RequestMapping(value = {"login", ""})
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleResourceRepository roleResourceRepository;
    @Autowired
    private ResourceRepository resourceRepository;

    @javax.annotation.Resource
    private RedisTemplate<String, String> redisTemplate;

    @Value("${sessiontimeout}")
    private Integer sessiontimeout;

    @Value("${cookietimeout}")
    private Integer cookietimeout;

    @Value("${defaultTargetAppCode}")
    private String defaultTargetAppCode;

    @Value("${uamAdminRoleCode}")
    private String uamAdminRoleCode;

    /**
     * login uri
     */
    private static final String LOGIN_URI = "login/login";

    /**
     * 重定向关键字
     */
    private static final String REDIRECT_KEYWORD = "redirect:";

    /**
     * 进入登录界面
     *
     * @return
     * @throws Exception
     * @throws IOException
     */
    @RequestMapping(path = {"login", ""}, method = {RequestMethod.GET, RequestMethod.OPTIONS})
    public ModelAndView login(@RequestParam(required = false) String targetUrl
            , @RequestParam(required = false) String targetAppCode, HttpServletResponse response, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName(LOGIN_URI);
        response.setHeader("Access-Control-Allow-Origin", "*");
        /*
         访问登录界面有两种情况，通过是否有target来区分：
         情况一，无targetUrl。表示要登录“统一授权系统”
         情况二，有targetUrl。表示重定向到“统一授权系统”
          */
        // 通过检查session中的user，判断是否登录过
        if (null == session.getAttribute(AllConstant.USERKEY)) {
            //对于未登录的情况，需要正常进入登录界面并将目标路径保存到session中
            mav.addObject(AllConstant.TARGETAPPCODE, targetAppCode);
            mav.addObject(AllConstant.TARGETURL, targetUrl);
        } else if (!Strings.isNullOrEmpty(targetAppCode)) {
            //对于已经登录的情况，直接创建ticket并重定向到目标路径
            String ticket = UUID.randomUUID().toString();
            JSONObject jObj = new JSONObject();
            jObj.put(AllConstant.USERKEY, session.getAttribute(AllConstant.USERKEY));
            //保存用户被授权的角色列表
            List<Role> commissionRoleList = new ArrayList<>();
            if (null != session.getAttribute(AllConstant.COMMISSION_ROLE_LIST)) {
                commissionRoleList = ((List<Role>) session.getAttribute(AllConstant.COMMISSION_ROLE_LIST)).stream().filter(r -> r.getAppCode().equals(targetAppCode)).collect(Collectors.toList());
            }
            jObj.put(AllConstant.COMMISSION_ROLE_LIST, commissionRoleList);
            //保存用户被授权的资源列表
            List<Resource> commissionResourceList = new ArrayList<>();
            if (null != session.getAttribute(AllConstant.COMMISSION_RESOURCE_LIST)) {
                commissionResourceList = ((List<Resource>) session.getAttribute(AllConstant.COMMISSION_RESOURCE_LIST)).stream().filter(r -> r.getAppCode().equals(targetAppCode)).collect(Collectors.toList());
            }
            jObj.put(AllConstant.COMMISSION_RESOURCE_LIST, commissionResourceList);
            BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(AllConstant.TICKETS);
            stringObjectObjectBoundHashOperations.put(ticket, jObj);
            stringObjectObjectBoundHashOperations.expire(24L, TimeUnit.HOURS);//设置24小时过期
            //重定向到目标路径并带有ticket
            mav.setViewName(REDIRECT_KEYWORD + targetUrl + "?ticket=" + ticket);
            return mav;
        }

        // 设置cookie为持久化cookie
        Cookie cookie = new Cookie("SESSION", session.getId());
        cookie.setMaxAge(cookietimeout);
        response.addCookie(cookie);
        return mav;
    }

    /**
     * 登录检查
     *
     * @return
     */
    @PostMapping("/check")
    public ModelAndView check(@RequestParam("loginName") String loginName, @RequestParam("password") String password,
                              @RequestParam("targetUrl") String targetUrl, @RequestParam("targetAppCode") String targetAppCode,
                              HttpSession session) {
        ModelAndView mav = new ModelAndView();
        log.info("登录信息：loginName:{} password:{}", loginName, password);
        //校验用户名密码是否正确
        //对密码进行加密
        User user = null;
        try {
            String encodePassword = HBEncrypt.shaDes(password);
            user = userRepository.findByLoginNameAndPassword(loginName, encodePassword);
            log.info("user信息：{}", user);
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        if (null == user) {
            mav.setViewName(LOGIN_URI);
            mav.addObject("msg", "登录失败！");
            return mav;
        }
        // 设置User对象
        session.setAttribute(AllConstant.USERKEY, user);

        //获取该用户的所有权限与资源
        List<UserRole> roleUserList = userRoleRepository.findByLoginName(user.getLoginName());
        //获取 该用户分配的 所有角色列表 并存入session
        List<Role> roleList = roleRepository.findByRoleCodeInOrderByRoleCode(roleUserList.stream().map(ur -> ur.getRoleCode()).collect(Collectors.toList()));
        session.setAttribute(AllConstant.COMMISSION_ROLE_LIST, roleList);
        List<RoleResource> roleResourceList = roleResourceRepository.findByRoleCodeIn(roleUserList.stream().map(ur -> ur.getRoleCode()).collect(Collectors.toList()));
        //获取 该用户分配的 所有资源列表 并存入session
        List<Resource> resourceList = resourceRepository.findByResourceCodeInOrderBySortNo(roleResourceList.stream().map(rr -> rr.getResourceCode()).collect(Collectors.toList()));
        session.setAttribute(AllConstant.COMMISSION_RESOURCE_LIST, resourceList);
        //获取 目标系统 资源列表
        List<Resource> targetResourceList = resourceList.stream().filter(r -> r.getAppCode().equals(targetAppCode)).collect(Collectors.toList());
        //获取 统一授权管理系统 资源列表 并存入session
        //如果拥有管理员角色，默认获得所有资源的访问权限
        List<Resource> uamCommissionResourceList;
        if (roleList.stream().filter(r -> r.getRoleCode().equals(uamAdminRoleCode)).count() > 0) {
            uamCommissionResourceList = resourceRepository.findByAppCodeOrderBySortNo(defaultTargetAppCode);
        } else {
            uamCommissionResourceList = resourceList.stream().filter(r -> r.getAppCode().equals(defaultTargetAppCode)).collect(Collectors.toList());
        }
        session.setAttribute(AllConstant.UAM_COMMISSION_RESOURCE_LIST, uamCommissionResourceList);
        List<Resource> uamResourceList = resourceRepository.findByAppCodeOrderBySortNo(defaultTargetAppCode);
        session.setAttribute(AllConstant.UAM_RESOURCE_LIST, uamResourceList);

        // 统一权限管理系统  不进行角色  资源  检查
        if (!(Strings.isNullOrEmpty(targetAppCode) || defaultTargetAppCode.equals(targetAppCode))) {
            //检查是否拥有目标系统中的角色
            if (roleList.stream().filter(r -> r.getAppCode().equals(targetAppCode)).count() == 0) {
                mav.setViewName(LOGIN_URI);
                mav.addObject("msg", "没有分配该系统的权限！");
                return mav;
            }
            //检查是否拥有目标系统中的资源
            if (targetResourceList.isEmpty()) {
                mav.setViewName(LOGIN_URI);
                mav.addObject("msg", "没有分配该系统的资源！");
                return mav;
            }

            //检查要调用的资源是否有访问权限
            try {
                URL url = new URL(targetUrl);
                String targetUri = url.getPath();
                if (targetResourceList.stream().filter(tr -> tr.getResourcePath().equals(targetUri)).count() == 0) {
                    mav.setViewName(LOGIN_URI);
                    mav.addObject("msg", "没有分配该资源！");
                    return mav;
                }
            } catch (Exception ex) {
                log.error("{}", ex);
            }
        }

        try {
            //清除旧target
            if (Strings.isNullOrEmpty(targetAppCode) || targetAppCode.equals(defaultTargetAppCode)) {
                //访问 统一权限管理系统
                if (Strings.isNullOrEmpty(targetUrl)) {
                    //默认访问主页
                    mav.setViewName(REDIRECT_KEYWORD + "/home/index");
                } else {
                    mav.setViewName(REDIRECT_KEYWORD + targetUrl);
                }
            } else {
                //其他系统重定向，需要创建一个临时ticket。
                // 设置跳转位置。生产一个随机ID，作为ticket。
                String ticket = UUID.randomUUID().toString();
                BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(AllConstant.TICKETS);
                JSONObject infoObj = new JSONObject();
                infoObj.put(AllConstant.USERKEY, user);
                infoObj.put(AllConstant.COMMISSION_ROLE_LIST, roleList);
                infoObj.put(AllConstant.COMMISSION_RESOURCE_LIST, resourceList);
                stringObjectObjectBoundHashOperations.put(ticket, infoObj);
                stringObjectObjectBoundHashOperations.expire(24L,TimeUnit.HOURS);//24小时过期
                //组装新url
                mav.setViewName(REDIRECT_KEYWORD + targetUrl + "?ticket=" + ticket);
                log.info("新ticket:{}", ticket);
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return mav;
    }

    /**
     * 提出登录
     *
     * @param session
     * @return
     */
    @GetMapping("logout")
    public String logout(HttpSession session, @RequestParam(required = false) String targetUrl
            , @RequestParam(required = false) String targetAppCode) {
        session.removeAttribute(AllConstant.USERKEY);
        session.setMaxInactiveInterval(0);

        String targetUri = REDIRECT_KEYWORD + "/" + LOGIN_URI;
        if (!Strings.isNullOrEmpty(targetUrl)) {
            targetUri += "?targetUrl=" + targetUrl + "&targetAppCode=" + targetAppCode;
        }

        // 退出后，进入到登录界面
        return targetUri;
    }

    /**
     * 验证ticket是否有效
     *
     * @param ticket
     * @return
     */
    @RequestMapping("checkTicket")
    @ResponseBody
    public JSONObject checkTicket(@RequestParam String ticket, HttpServletRequest request) {
        // 验证ticket是否存在，如果存在就返回相应内容，同时删除这个ticket，防止别人盗用。
        log.info("请求地址{}，收到ticket:{}", request.getRemoteAddr(), ticket);
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(AllConstant.TICKETS);
        JSONObject infoObj = new JSONObject();
        if (null != stringObjectObjectBoundHashOperations.get(ticket)) {
            infoObj = (JSONObject) stringObjectObjectBoundHashOperations.get(ticket);
            log.info("ticket:{}，内容：{}", ticket, infoObj.toJSONString());
//            stringObjectObjectBoundHashOperations.delete(ticket);
        }
        // 验证ticket是否正确
        return infoObj;
    }
}
