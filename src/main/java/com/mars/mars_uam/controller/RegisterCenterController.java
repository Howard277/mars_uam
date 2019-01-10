package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册中心控制器
 * Created by wuketao on 2018/8/16.
 */
@Slf4j
@Controller
@RequestMapping("registerCenter")
public class RegisterCenterController {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * 注册中心地址
     */
    private String registerCenterRootUrl;

    /**
     * span标签
     */
    private static String spanLable = "<span></span>";

    /**
     * 注册中心信息 视图
     *
     * @return
     */
    @GetMapping("registerCenterInfo")
    public ModelAndView registerCenterInfo() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("registerCenter/serviceInstanceList");
        mav.addObject("title", "注册中心-服务实例列表");
        mav.addObject("applications", Strings.isNullOrEmpty(registerCenterRootUrl) ? null : getRegisterCenterServiceInstanceList());
        mav.addObject("registerCenterUrl", registerCenterRootUrl);
        return mav;
    }

    /**
     * 获取注册中心，服务实例列表
     */
    private JSONObject getRegisterCenterServiceInstanceList() {
        JSONObject result = null;
        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(registerCenterRootUrl + "/eureka/apps", JSONObject.class);
            result = responseEntity.getBody();
        } catch (Exception ex) {
            log.error("获取注册中心信息时发生异常：{}", ex);
        }
        return result;
    }

    /**
     * 获取注册中心上某服务的信息
     *
     * @param appName
     * @return
     */
    @GetMapping("getRegisterCenterAppInfo")
    @ResponseBody
    public String getRegisterCenterAppInfo(@RequestParam String appName) {
        StringBuilder html = new StringBuilder();
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(registerCenterRootUrl + "/eureka/apps/" + appName, JSONObject.class);
        JSONObject responseBody = responseEntity.getBody();
        JSONArray instanceArray = responseBody.getJSONObject("application").getJSONArray("instance");
        for (int index = 0; index < instanceArray.size(); index++) {
            JSONObject jObj = instanceArray.getJSONObject(index);
            html.append(" <div class='divServiceInstance' onmouseover=\"javascript:$(this).addClass('divServiceInstanceMouseOver');\" onmouseleave=\"javascript:$(this).removeClass('divServiceInstanceMouseOver');\">");
            html.append(" <p style='font-size:20px;'>服务名：<span>" + jObj.getString("app") + spanLable);
            html.append(" <p style='font-size:20px;'>实例名：<span>" + jObj.getString("instanceId") + spanLable);
            html.append(" <p>ip地址：<span>" + jObj.getString("ipAddr") + spanLable);
            html.append(" <p>health地址：<a target='_blank' href='" + jObj.getString("healthCheckUrl") + "' >" + jObj.getString("healthCheckUrl") + "</a></p>");
            if (jObj.getString("status").equals("UP")) {
                html.append(" <p> 状态：<span style='color:green;'>在线（<span>" + jObj.getString("status") + "</span>）</span></p>");
                html.append(" <br/>");
                html.append(" <input class='btn btn-danger radius' type='button' value='实例下线'  onclick=\"changeServiceStatus('" + appName + "','" + jObj.getString("instanceId") + "','OUT_OF_SERVICE')\"/>");
            } else {
                html.append(" <p> 状态：<span style='color:red;'>离线（<span>" + jObj.getString("status") + "</span>）</span></p>");
                html.append(" <br/>");
                html.append(" <input class='btn btn-success radius' type='button' value='实例上线' onclick=\"changeServiceStatus('" + appName + "','" + jObj.getString("instanceId") + "','UP')\"/>");
            }
            html.append(" </div>");
        }
        return html.toString();
    }

    /**
     * 修改服务实例状态
     *
     * @param body
     * @return
     */
    @PostMapping("changeServiceStatus")
    @ResponseBody
    public Map<String, Object> changeServiceStatus(@RequestBody JSONObject body) {
        String appId = body.getString("appId");
        String instanceId = body.getString("instanceId");
        String targetStatus = body.getString("targetStatus");
        String targetUrl = String.format(registerCenterRootUrl + "/eureka/apps/%s/%s/status?value=%s", appId, instanceId, targetStatus);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("flag", false);
        resultMap.put("msg", "状态修改失败！");
        restTemplate.put(targetUrl, null);
        resultMap.put("flag", true);
        resultMap.put("msg", "状态修改成功！");

        return resultMap;
    }

    /**
     * 设置注册中心地址
     *
     * @param url
     * @return
     */
    @GetMapping("setRegisterCenterUrl")
    @ResponseBody
    public JSONObject setRegisterCenterUrl(@RequestParam String url) {
        JSONObject result = new JSONObject();
        result.put("flag", false);
        result.put("msg", "设置失败！");
        this.registerCenterRootUrl = url;
        result.put("flag", true);
        result.put("msg", "设置成功！");
        return result;
    }
}
