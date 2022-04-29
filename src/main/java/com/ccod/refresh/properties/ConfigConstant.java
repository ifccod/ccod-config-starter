package com.ccod.refresh.properties;

/**
 * 配置常量
 *
 * @author ccod
 * @date 2022/4/29 10:43 AM
 **/
public class ConfigConstant {

    /**
     * 全局配置前缀
     */
    public static final String REFRESH_CONFIG_KEY_PREFIX = "ccod.custom";

    /**
     * 资源获取器配置key
     */
    public static final String DEFAULT_PROVIDE_CLASS_NAME_KEY = REFRESH_CONFIG_KEY_PREFIX + ".refresh.provide";

    /**
     * 默认的远程资源获取器
     */
    public static final String DEFAULT_PROVIDE = "com.ccod.refresh.provide.impl.DefaultRedisSourceProvide";

    /**
     * 注解parse处理类key
     */
    public static final String DEFAULT_ANN_PARSER_KEY = REFRESH_CONFIG_KEY_PREFIX + ".ann.parse";

    /**
     * 默认的parse处理类实现
     */
    public static final String DEFAULT_ANN_PARSER = "com.ccod.refresh.parse.impl.SpringValueParse";
}
