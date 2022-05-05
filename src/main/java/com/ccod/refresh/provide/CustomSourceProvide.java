package com.ccod.refresh.provide;

import com.ccod.refresh.properties.CustomRefreshContext;
import com.google.common.collect.Lists;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    default List<String> refresh() {
        ConfigurableEnvironment environment = this.getEnvironment();
        PropertySource propertySource = environment.getPropertySources().get(CustomRefreshContext.SOURCE_NAME);
        if (propertySource == null) {
            return null;
        }
        List<String> res = Lists.newArrayList();
        Map<String, Object> oldSource = (Map) propertySource.getSource();
        Map<String, Object> sourceMap = this.getSource();
        if (CollectionUtils.isEmpty(sourceMap)) {
            return null;
        }
        sourceMap.forEach((sourceKey, sourceValue) -> {
            Object oldValue = oldSource.get(sourceKey);
            if (!Objects.equals(sourceValue, oldValue) && sourceValue != null) {
                oldSource.put(sourceKey, sourceValue);
                res.add(sourceKey);
            }
        });
        return res;
    }

    /**
     * 设置环境上下文
     *
     * @param environment
     */
    void setEnvironment(ConfigurableEnvironment environment);

    /**
     * 获取环境变量
     *
     * @return
     */
    ConfigurableEnvironment getEnvironment();

    /**
     * 关闭资源
     * spring上下文关闭时会执行这个方法，实现你自定义的销毁逻辑
     */
    void close();

}
