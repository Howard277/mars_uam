package com.mars.mars_uam.constant;

/**
 * 常量定义
 *
 * @author wuketao
 */
public class AllConstant {
    /**
     * 私有构造函数
     */
    private AllConstant() {
    }

    /**
     * DES密码
     */
    public static final String DESKEY = "123456781234567812345678";

    /**
     * URI跟路径
     */
    public static final String URIROOTPATH = "/";
    /**
     * 用户信息在session中的key
     */
    public static final String USERKEY = "user";
    /**
     * 用户授权角色集合信息在session中的key
     */
    public static final String COMMISSION_ROLE_LIST = "commission_role_list";
    /**
     * 用户授权资源集合信息在session中的key
     */
    public static final String COMMISSION_RESOURCE_LIST = "commission_resource_list";
    /**
     * 统一授权管理 授权资源集合信息在session中的key
     */
    public static final String UAM_COMMISSION_RESOURCE_LIST = "uam_commission_resource_list";
    /**
     * 统一授权管理 资源集合信息在session中的key
     */
    public static final String UAM_RESOURCE_LIST = "uam_resource_ist";
    /**
     * redis中保存ticket的map集合的key
     */
    public static final String TICKETS = "TICKETS";
    /**
     * 目标URL
     */
    public static final String TARGETURL = "targetUrl";
    /**
     * 目标应用系统编码
     */
    public static final String TARGETAPPCODE = "targetAppCode";


}
