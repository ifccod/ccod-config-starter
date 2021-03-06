package com.ccod.refresh.support;

import com.ccod.refresh.properties.CustomRefreshContext;
import com.ccod.refresh.provide.CustomSourceProvide;
import com.ccod.refresh.util.RefreshBeanUtil;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ccod
 * @date 2022/3/3 6:55 PM
 **/
public class DoRefreshJob implements BeanFactoryPostProcessor {

    private static final Log log = LogFactory.getLog(DoRefreshJob.class);

    private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    public void doJob() {
        List<CustomSourceProvide> customSourceProvideList = CustomRefreshContext.getCustomSourceProvideList();
        if (CollectionUtils.isEmpty(customSourceProvideList)) {
            log.warn("自动刷新未注册资源解析器");
            return;
        }
        service.scheduleWithFixedDelay(() -> {
            List<String> refreshList = Lists.newArrayList();
            try {
                for (CustomSourceProvide customSourceProvide : customSourceProvideList) {
                    List<String> customSourceList = customSourceProvide.refresh();
                    if (!CollectionUtils.isEmpty(customSourceList)) {
                        refreshList.addAll(customSourceList);
                    }
                }
                if (!CollectionUtils.isEmpty(refreshList)) {
                    for (String key : refreshList) {
                        try {
                            RefreshBeanUtil.refresh(key);
                        } catch (Exception ex) {
                            log.error("key:[" + key + "] 刷新失败");
                        }
                    }
                }
            } catch (Throwable ex) {
                log.error("refresh error", ex);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void close() {
        service.shutdown();
        CustomRefreshContext.close();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        RefreshBeanUtil.beanFactory = beanFactory;
        RefreshBeanUtil.typeConverter = beanFactory.getTypeConverter();
    }
}
