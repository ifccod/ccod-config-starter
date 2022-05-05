package com.ccod.refresh.processor;

import com.ccod.refresh.parse.CustomAnnotationParse;
import com.ccod.refresh.properties.ConfigConstant;
import com.ccod.refresh.util.ReflectUtils;
import com.ccod.refresh.util.RefreshBeanUtil;
import com.ccod.refresh.util.SpringValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ccod
 * @date 2022/4/28 4:05 PM
 **/
public class CustomRefreshBeanPostProcessor implements BeanPostProcessor {

    private List<CustomAnnotationParse> customAnnotationParseList;

    public CustomRefreshBeanPostProcessor(Environment environment) {
        String customAnnotationParse = environment.getProperty(ConfigConstant.DEFAULT_ANN_PARSER_KEY, ConfigConstant.DEFAULT_ANN_PARSER);
        customAnnotationParseList = ReflectUtils.newTargetList(customAnnotationParse, CustomAnnotationParse.class);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (CollectionUtils.isEmpty(customAnnotationParseList)) {
            return bean;
        }
        Class<?> targetClass = bean.getClass();
        List<Field> allField = ReflectUtils.findAllField(targetClass);
        if (!CollectionUtils.isEmpty(allField)) {
            allField.forEach(item -> processField(bean, beanName, item));
        }
        List<Method> allMethod = ReflectUtils.findAllMethod(targetClass);
        if (!CollectionUtils.isEmpty(allMethod)) {
            allMethod.forEach(item -> processMethod(bean, beanName, item));
        }
        return bean;
    }

    private void processField(Object bean, String beanName, Field field) {
        for (CustomAnnotationParse customAnnotationParse : customAnnotationParseList) {
            List<SpringValue> springValueList = customAnnotationParse.parseField(bean, beanName, field);
            if (!CollectionUtils.isEmpty(springValueList)) {
                springValueList.forEach(item -> RefreshBeanUtil.putSpringValue(item));
            }
        }
    }

    private void processMethod(Object bean, String beanName, Method method) {
        for (CustomAnnotationParse customAnnotationParse : customAnnotationParseList) {
            List<SpringValue> springValueList = customAnnotationParse.parseMethod(bean, beanName, method);
            if (!CollectionUtils.isEmpty(springValueList)) {
                springValueList.forEach(item -> RefreshBeanUtil.putSpringValue(item));
            }
        }
    }
}
