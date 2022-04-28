package com.ccod.refresh.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author ccod
 * @date 2022/4/28 4:10 PM
 **/
@Slf4j
public class RefreshBeanUtil {

    public static PlaceholderHelper placeholderHelper = new PlaceholderHelper();

    private static boolean typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();

    private static final Gson gson = new Gson();

    public static ConfigurableBeanFactory beanFactory;

    public static TypeConverter typeConverter;

    /**
     * key = @value.value
     */
    @Getter
    private static Map<String, RefreshBO> valueMap = Maps.newConcurrentMap();

    /**
     * 刷新
     *
     * @param key
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void refresh(String key) throws InvocationTargetException, IllegalAccessException {
        RefreshBO refreshBO = valueMap.get(key);
        if (refreshBO == null) {
            return;
        }
        List<SpringValue> springValueList = refreshBO.getSpringValueList();
        if (CollectionUtils.isEmpty(springValueList)) {
            return;
        }
        for (SpringValue springValue : springValueList) {
            Object newValue = resolvePropertyValue(springValue);
            springValue.update(newValue);
            log.info("beanName:{},field:{}【@value('{}')】 配置修改  new:{}", springValue.getBeanName(), springValue.getField().getName(), springValue.getPlaceholder(), newValue);
        }
    }

    private static Object resolvePropertyValue(SpringValue springValue) {
        TypeConverter typeConverter = beanFactory.getTypeConverter();
        // value will never be null, as @Value and @ApolloJsonValue will not allow that
        Object value = placeholderHelper
                .resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());

        if (springValue.isJson()) {
            value = parseJsonValue((String) value, springValue.getGenericType());
        } else {
            if (springValue.isField()) {
                // org.springframework.beans.TypeConverter#convertIfNecessary(java.lang.Object, java.lang.Class, java.lang.reflect.Field) is available from Spring 3.2.0+
                if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
                    value = typeConverter
                            .convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
                } else {
                    value = typeConverter.convertIfNecessary(value, springValue.getTargetType());
                }
            } else {
                value = typeConverter.convertIfNecessary(value, springValue.getTargetType(),
                        springValue.getMethodParameter());
            }
        }
        return value;
    }

    private static Object parseJsonValue(String json, Type targetType) {
        try {
            return gson.fromJson(json, targetType);
        } catch (Throwable ex) {
            log.error("Parsing json '{}' to type {} failed!", json, targetType, ex);
            throw ex;
        }
    }

    private static boolean testTypeConverterHasConvertIfNecessaryWithFieldParameter() {
        try {
            TypeConverter.class.getMethod("convertIfNecessary", Object.class, Class.class, Field.class);
        } catch (Throwable ex) {
            return false;
        }

        return true;
    }

}
