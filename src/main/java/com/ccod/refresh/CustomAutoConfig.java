package com.ccod.refresh;

import com.ccod.refresh.processor.CustomRefreshBeanPostProcessor;
import com.ccod.refresh.properties.ConfigConstant;
import com.ccod.refresh.support.DoRefreshJob;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author ccod
 * @date 2022/4/28 3:19 PM
 **/
@Configuration
@ConditionalOnProperty(prefix = ConfigConstant.REFRESH_CONFIG_KEY_PREFIX, name = "enable", havingValue = "true")
public class CustomAutoConfig {

    @Bean(initMethod = "doJob", destroyMethod = "close")
    public DoRefreshJob refreshJob() {
        return new DoRefreshJob();
    }

    @Bean
    public CustomRefreshBeanPostProcessor customRefreshBeanPostProcessor(ConfigurableEnvironment environment) {
        return new CustomRefreshBeanPostProcessor(environment);
    }

}
