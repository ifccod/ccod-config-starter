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
     * 从远程数据源获取 key-value形式的自定义属性
     *
     * @return
     */
    Map<String, Object> getSource();

    /**
     * 执行刷新
     * 此逻辑内部将变更的配置set到environment内部并返回变更后的key
     *
     * @return 需要刷新的集合
     */
    List<String> refresh();

    /**
     * 设置环境上下文
     *
     * @param environment
     */
    void setEnvironment(ConfigurableEnvironment environment);

    /**
     * 关闭资源
     * spring上下文关闭时会执行这个方法，实现你自定义的销毁逻辑
     */
    void close();

}
