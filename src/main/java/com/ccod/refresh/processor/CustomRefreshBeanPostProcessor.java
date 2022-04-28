package com.ccod.refresh.processor;

import com.ccod.refresh.util.RefreshBO;
import com.ccod.refresh.util.RefreshBeanUtil;
import com.ccod.refresh.util.SpringValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author ccod
 * @date 2022/4/28 4:05 PM
 **/
@Slf4j
public class CustomRefreshBeanPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        List<Field> allField = findAllField(targetClass);
        if (!CollectionUtils.isEmpty(allField)) {
            allField.forEach(item -> processField(bean, beanName, item));
        }
        List<Method> allMethod = findAllMethod(targetClass);
        if (!CollectionUtils.isEmpty(allMethod)) {
            allMethod.forEach(item -> processMethod(bean, beanName, item));
        }
        return bean;
    }

    private void putSpringValue(SpringValue springValue, String key) {
        RefreshBO refreshBO = RefreshBeanUtil.getValueMap().get(key);
        if (refreshBO == null) {
            refreshBO = new RefreshBO();
            RefreshBeanUtil.getValueMap().put(key, refreshBO);
        }
        refreshBO.getSpringValueList().add(springValue);
    }

    protected void processField(Object bean, String beanName, Field field) {
        // register @Value on field
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Set<String> keys = RefreshBeanUtil.placeholderHelper.extractPlaceholderKeys(value.value());

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, field, false);
            try {
                putSpringValue(springValue, key);
            } catch (Exception ex) {
                log.error("putSpringValue error,beanName:{}", beanName, ex);
            }
        }
    }

    protected void processMethod(Object bean, String beanName, Method method) {
        //register @Value on method
        Value value = method.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        //skip Configuration bean methods
        if (method.getAnnotation(Bean.class) != null) {
            return;
        }
        if (method.getParameterTypes().length != 1) {
            log.error("Ignore @Value setter {}.{}, expecting 1 parameter, actual {} parameters", bean.getClass().getName(), method.getName(), method.getParameterTypes().length);
            return;
        }

        Set<String> keys = RefreshBeanUtil.placeholderHelper.extractPlaceholderKeys(value.value());

        if (keys.isEmpty() || keys.size() > 1) {
            return;
        }

        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, method, false);
            putSpringValue(springValue, key);
        }
    }

    private List<Field> findAllField(Class clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                res.add(field);
            }
        });
        return res;
    }

    private List<Method> findAllMethod(Class clazz) {
        final List<Method> res = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                res.add(method);
            }
        });
        return res;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        RefreshBeanUtil.beanFactory = beanFactory;
        RefreshBeanUtil.typeConverter = beanFactory.getTypeConverter();
    }
}
