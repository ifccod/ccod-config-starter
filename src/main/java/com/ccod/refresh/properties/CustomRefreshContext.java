package com.ccod.refresh.properties;

import com.ccod.refresh.provide.CustomSourceProvide;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author ccod
 * @date 2022/4/28 11:14 AM
 **/
@Slf4j
public class CustomRefreshContext {

    public static final String SOURCE_NAME = "CustomMadeEnvironmentPostProcessor_";

    public static final String REDIS_REFRESH_KEU = "spring:config:map";

    public static final String REFRESH_CONFIG_KEY_PREFIX = "ccod.custom";

    private static final String DEFAULT_PROVIDE = "com.sky.config.refresh.com.ccod.refresh.provide.impl.DefaultRedisSourceProvide";

    @Getter
    @Setter
    private static List<CustomSourceProvide> customSourceProvideList;

    public static void setCustomSourceProvideList(ConfigurableEnvironment environment) {
        CustomRefreshContext.customSourceProvideList = Lists.newArrayList();
        String provideClassName = environment.getProperty(REFRESH_CONFIG_KEY_PREFIX + ".com.ccod.refresh.provide", DEFAULT_PROVIDE);
        try {
            Constructor provideConstructor = ReflectionUtils.accessibleConstructor(Class.forName(provideClassName));
            customSourceProvideList.add((CustomSourceProvide) provideConstructor.newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            log.error("{} 初始化失败", provideClassName, ex);
            throw new BeanCreationException("资源加载器初始化失败 ");
        }
    }

    public static void close() {
        if (!CollectionUtils.isEmpty(customSourceProvideList)) {
            for (CustomSourceProvide customSourceProvide : customSourceProvideList) {
                customSourceProvide.close();
            }
        }
    }
}
