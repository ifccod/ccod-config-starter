package com.ccod.refresh.provide;

import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.Map;

/**
 * 自定义属性值来源
 *
 * @author ccod
 * @date 2022/4/28 10:52 AM
 **/
public interface CustomSourceProvide extends Cloneable {

    /**
     * 获取自定义属性值
     *
     * @return
     */
    Map<String, Object> getSource();

    /**
     * 执行刷新
     * @return 需要刷新的集合
     */
    List<String> refresh();

    /**
     * @param environment
     */
    void setEnvironment(ConfigurableEnvironment environment);

    /**
     * 关闭资源
     */
    void close();

}
