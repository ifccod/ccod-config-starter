package com.ccod.refresh.properties;

import com.ccod.refresh.provide.CustomSourceProvide;
import com.ccod.refresh.util.ReflectUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author ccod
 * @date 2022/4/28 11:14 AMw
 **/
@Slf4j
public class CustomRefreshContext {

    public static final String SOURCE_NAME = "CustomMadeEnvironmentValues_";

    @Getter
    @Setter
    private static List<CustomSourceProvide> customSourceProvideList;

    public static void setCustomSourceProvideList(ConfigurableEnvironment environment) {
        CustomRefreshContext.customSourceProvideList = Lists.newArrayList();
        String provideClassName = environment.getProperty(ConfigConstant.DEFAULT_PROVIDE_CLASS_NAME_KEY, ConfigConstant.DEFAULT_PROVIDE);
        CustomRefreshContext.customSourceProvideList = ReflectUtils.newTargetList(provideClassName, CustomSourceProvide.class);
    }

    public static void close() {
        if (!CollectionUtils.isEmpty(customSourceProvideList)) {
            for (CustomSourceProvide customSourceProvide : customSourceProvideList) {
                customSourceProvide.close();
            }
        }
    }

}
