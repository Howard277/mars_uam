package com.mars.mars_uam.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.mars.mars_uam.annotation.SystemLogAnnotation;
import com.mars.mars_uam.constant.AllConstant;
import com.mars.mars_uam.constant.EnableEnum;
import com.mars.mars_uam.entity.City;
import com.mars.mars_uam.entity.Organization;
import com.mars.mars_uam.entity.User;
import com.mars.mars_uam.repository.CityRepository;
import com.mars.mars_uam.repository.OrganizationRepository;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织机构信息
 * Created by wuketao on 2018/2/12.
 */
@SystemLogAnnotation
@Transactional
@Slf4j
@RestController
@RequestMapping("api/org")
public class ApiOrganizationController {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private CityRepository cityRepository;

    private static String cityCodeStr = "cityCode";
    //城市编码：全国
    private static String cityCodeNationValue = "1001";

    @Value("${productSystemRootUrl}")
    private String productSystemRootUrl;
    @Autowired
    private RestTemplate restTemplate;


    /**
     * 通过调节获取适用门店
     *
     * @param lendingChannel 放款渠道
     * @param loanTerm       贷款期数
     * @param productNo      产品类型
     * @return
     */
    @GetMapping("findAllByCondition")
    public Set<Organization> findAllByCondition(@RequestParam String lendingChannel,
                                                @RequestParam String loanTerm, @RequestParam String productNo) {
        //首先获取所有启用的组织机构信息
        List<Organization> orgList = organizationRepository.findByEnable(EnableEnum.ENABLED);
        //通过查询条件，调用产品系统，获取准入配置门店
        JSONObject requestJO = new JSONObject();
        requestJO.put("lendingChannel", lendingChannel);
        requestJO.put("loanTerm", loanTerm);
        requestJO.put("productNo", productNo);
        ResponseEntity<JSONObject> responseJO = restTemplate.postForEntity(productSystemRootUrl + "/api/orgChannel", requestJO, JSONObject.class);
        JSONArray allowStoreJSONArray = responseJO.getBody().getJSONArray("data");
        Set<String> allowOrgIdSet = new HashSet<>();
        for (int i = 0; i < allowStoreJSONArray.size(); i++) {
            allowOrgIdSet.add(allowStoreJSONArray.getJSONObject(i).getString("orgId"));
        }
        //通过准入配置门店，过滤组织机构集合
        Set<Organization> orgAllowSet = orgList.stream().filter(o -> allowOrgIdSet.contains(o.getOrgCode())).collect(Collectors.toSet());
        addParentOrg(orgList, orgAllowSet);
        return orgAllowSet;
    }

    /**
     * 递归添加父级机构
     *
     * @param orgList
     * @param orgAllowSet
     */
    private void addParentOrg(List<Organization> orgList, Set<Organization> orgAllowSet) {
        Set<Organization> tempSet = new HashSet<>();
        //遍历准入机构
        for (Organization org : orgAllowSet) {
            //当准入机构的父级机构编码为非空时，查找父级机构
            if (StringUtils.isNotBlank(org.getParentOrgCode())) {
                for (Organization pOrg : orgList) {
                    //找到父级机构，同时该机构不在现有列表中，则添加到临时列表，并退出本地循环。
                    if (pOrg.getOrgCode().equals(org.getParentOrgCode()) && !orgAllowSet.contains(pOrg)) {
                        tempSet.add(pOrg);
                        break;
                    }
                }
            }
        }
        //如果此次循环有添加新的父级，就将新父级添加到准入列表，并递归调用
        if (!tempSet.isEmpty()) {
            orgAllowSet.addAll(tempSet);
            addParentOrg(orgList, orgAllowSet);
        }
    }

    /**
     * 获取组织机构列表
     *
     * @return
     */
    @GetMapping("findAll")
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }


    /**
     * 获取"启用"状态的组织机构列表
     *
     * @return
     */
    @GetMapping("findAllEnabled")
    public List<Organization> findAllEnabled() {
        return organizationRepository.findByEnable(EnableEnum.ENABLED);
    }


    /**
     * 获取组织机构对应的来源城市
     *
     * @param orgCode
     * @return
     */
    @GetMapping("getOrgSourceCity")
    public JSONArray getOrgSourceCity(@RequestParam String orgCode) {
        JSONArray jsonArray = new JSONArray();
        try {
            Organization org = organizationRepository.findByOrgCode(orgCode);
            String strSourceCity = org.getSourceCity();
            if (!Strings.isNullOrEmpty(strSourceCity)) {
                jsonArray = JSONArray.parseArray(strSourceCity);
                for (int index = 0; index < jsonArray.size(); index++) {
                    //如果辐射城市中包含“全国”，就直接从城市列表中获取除“全国”之外的所有城市
                    if (jsonArray.getJSONObject(index).get(cityCodeStr).toString().equals(cityCodeNationValue)) {
                        jsonArray = new JSONArray();//清空就数据
                        List<City> cityList = cityRepository.findAll();
                        List<City> cityList2 = cityList.stream()
                                .filter(c -> !c.getCityCode().equals(cityCodeNationValue)) //去掉“全国”
                                .collect(Collectors.toList());
                        for (City city : cityList2) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("cityCode", city.getCityCode());
                            map.put("cityName", city.getCityName());
                            jsonArray.add(new JSONObject(map));
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return jsonArray;
    }

    /**
     * 获取组织机构
     *
     * @param orgCode
     * @return
     */
    @GetMapping("getOrganization")
    public Organization getOrganization(@RequestParam String orgCode) {
        Organization org = null;
        try {
            org = organizationRepository.findByOrgCode(orgCode);
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return org;
    }

    /**
     * 获取组织机构列表
     *
     * @return
     */
    @ApiOperation("获取组织机构列表")
    @GetMapping("orgFind")
    public List<Organization> orgFind(Organization org) {

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase(true) //改变默认大小写忽略方式：忽略大小写
                .withMatcher("orgCode", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("orgName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("orgLevel", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("parentOrgCode", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("parentOrgName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("address", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("cityCode", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("cityName", ExampleMatcher.GenericPropertyMatchers.contains());
        //创建实例
        Example<Organization> ex = Example.of(org, matcher);

        return organizationRepository.findAll(ex);
    }

    /**
     * 获取组织机构分页数据
     *
     * @return
     */
    @PostMapping("orgPage")
    public Page<Organization> orgPage(@RequestParam Integer pageIndex, @RequestParam Integer pageSize,
                                      @RequestParam(required = false) String searchCondition) {
        Page<Organization> organizationPage = null;
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize);
        if (Strings.isNullOrEmpty(searchCondition)) {
            return organizationRepository.findAll(pageable);
        } else {
            searchCondition = "%" + searchCondition + "%";
            return organizationRepository.findByOrgCodeLikeOrOrgNameLikeOrParentOrgCodeLikeOrParentOrgNameLikeOrCityCodeLikeOrCityNameLike(searchCondition, searchCondition, searchCondition, searchCondition, searchCondition, searchCondition, pageable);
        }
    }

    /**
     * 删除组织机构
     *
     * @param orgCode
     * @return
     */
    @PostMapping("deleteOrg")
    public Map<String, Object> deleteOrg(@RequestParam String orgCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("flag", false);
        try {
            Organization org = new Organization();
            org.setOrgCode(orgCode);
            organizationRepository.delete(org);
            map.put("flag", true);
        } catch (Exception ex) {
            map.put("msg", "删除失败！");
            log.error("{}", ex);
        }
        return map;
    }

    /**
     * 保存 组织结构信息
     *
     * @param organization 组织结构信息
     * @return
     */
    @PostMapping("saveOrg")
    public Map<String, Object> saveOrg(@RequestBody Organization organization, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("flag", false);

        //通过查找旧记录是否存在，判断是新增还是修改。
        Organization oldOrg = organizationRepository.findByOrgCode(organization.getOrgCode());
        if (null == oldOrg) {
            organization.setCreateTime(new Date());
            organization.setCreator(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        } else {
            organization.setCreator(oldOrg.getCreator());
            organization.setCreateTime(oldOrg.getCreateTime());
        }

        organization.setUpdateTime(new Date());
        organization.setUpdateUser(((User) session.getAttribute(AllConstant.USERKEY)).getLoginName());
        organizationRepository.save(organization);
        result.put("flag", true);

        return result;
    }

    /**
     * 检查 机构编码是否被占用
     *
     * @param orgCode
     * @return
     */
    @GetMapping("checkOrgCode")
    public JSONObject checkOrgCode(@RequestParam String orgCode) {
        JSONObject jObj = new JSONObject();
        jObj.put("flag", false);
        jObj.put("msg", "编码已存在！");

        //从数据库中查找编码是否被使用
        Organization org = organizationRepository.findByOrgCode(orgCode);
        if (null == org) {
            jObj.put("flag", true);
            jObj.remove("msg");
        }

        return jObj;
    }
}
