package com.ccod.refresh.parse;

import com.ccod.refresh.util.SpringValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义注解解析
 * 解析字段或方法上带的注解，用来打入上下文为bean提供自动刷新的功能
 *
 * @author ccod
 * @date 2022/4/29 2:50 PM
 **/
public interface CustomAnnotationParse {

    /**
     * 解析bean字段，返回解析元数据信息
     *
     * @param bean
     * @param beanName
     * @param field
     * @return
     */
    List<SpringValue> parseField(Object bean, String beanName, Field field);

    /**
     * 解析方法，返回解析元数据信息
     * @param bean
     * @param beanName
     * @param method
     * @return
     */
    List<SpringValue> parseMethod(Object bean, String beanName, Method method);

}
