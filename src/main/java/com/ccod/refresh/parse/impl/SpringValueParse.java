package com.ccod.refresh.parse.impl;

import com.ccod.refresh.parse.CustomAnnotationParse;
import com.ccod.refresh.util.RefreshBeanUtil;
import com.ccod.refresh.util.SpringValue;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * spring value注解解析器
 *
 * @author ccod
 * @date 2022/4/29 2:57 PM
 **/
public class SpringValueParse implements CustomAnnotationParse {

    private static final Log log = LogFactory.getLog(SpringValueParse.class);

    @Override
    public List<SpringValue> parseField(Object bean, String beanName, Field field) {
        // register @Value on field
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return null;
        }
        Set<String> keys = RefreshBeanUtil.placeholderHelper.extractPlaceholderKeys(value.value());

        if (keys.isEmpty()) {
            return null;
        }
        List<SpringValue> res = Lists.newArrayList();
        for (String key : keys) {
            res.add(new SpringValue(key, value.value(), bean, beanName, field, false));
        }
        return res;
    }

    @Override
    public List<SpringValue> parseMethod(Object bean, String beanName, Method method) {
        //register @Value on method
        Value value = method.getAnnotation(Value.class);
        if (value == null) {
            return null;
        }
        //skip Configuration bean methods
        if (method.getAnnotation(Bean.class) != null) {
            return null;
        }
        if (method.getParameterTypes().length != 1) {
            log.error("Ignore @Value setter " + bean.getClass().getName() + "." + method.getName() + ", expecting 1 parameter, actual " + method.getParameterTypes().length + " parameters");
            return null;
        }
        Set<String> keys = RefreshBeanUtil.placeholderHelper.extractPlaceholderKeys(value.value());
        if (keys.isEmpty() || keys.size() > 1) {
            return null;
        }
        List<SpringValue> res = Lists.newArrayList();

        for (String key : keys) {
            res.add(new SpringValue(key, value.value(), bean, beanName, method, false));
        }
        return res;
    }
}
