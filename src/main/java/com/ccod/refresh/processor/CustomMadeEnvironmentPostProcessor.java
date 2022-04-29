package com.ccod.refresh.processor;

import com.ccod.refresh.properties.ConfigConstant;
import com.ccod.refresh.properties.CustomRefreshContext;
import com.ccod.refresh.provide.CustomSourceProvide;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author ccod
 * @date 2022/4/28 10:37 AM
 **/
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class CustomMadeEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getPropertySources().contains(CustomRefreshContext.SOURCE_NAME)) {
            log.info("{} 已注册", CustomRefreshContext.SOURCE_NAME);
            return;
        }
        String property = environment.getProperty(ConfigConstant.REFRESH_CONFIG_KEY_PREFIX + ".enable", "false");
        if (!"true".equals(property)) {
            log.info("自动刷新功能未开启");
            return;
        }
        CustomRefreshContext.setCustomSourceProvideList(environment);
        List<CustomSourceProvide> customSourceProvideList = CustomRefreshContext.getCustomSourceProvideList();
        Map<String, Object> sourceMap = Maps.newConcurrentMap();
        for (CustomSourceProvide customSourceProvide : customSourceProvideList) {
            customSourceProvide.setEnvironment(environment);
            Map<String, Object> customSourceProvideMap = customSourceProvide.getSource();
            if (!CollectionUtils.isEmpty(customSourceProvideMap)) {
                sourceMap.putAll(customSourceProvideMap);
            }
        }
        environment.getPropertySources().addFirst(new MapPropertySource(CustomRefreshContext.SOURCE_NAME, sourceMap));
    }
}
