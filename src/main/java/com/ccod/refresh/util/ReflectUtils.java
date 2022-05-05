package com.ccod.refresh.util;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ccod
 * @date 2022/4/29 3:06 PM
 **/
public class ReflectUtils {

    private static final Log log = LogFactory.getLog(ReflectUtils.class);

    /**
     * 通过配置创建配置实现类列表
     *
     * @param config
     * @param targetType
     * @param <T>
     * @return
     */
    public static <T> List<T> newTargetList(String config, Class<T> targetType) {
        List<T> res = Lists.newArrayList();
        String[] configList = config.split(",");
        for (int i = 0; i < configList.length; i++) {
            try {
                Constructor targetConstructor = ReflectionUtils.accessibleConstructor(Class.forName(configList[i]));
                res.add((T) targetConstructor.newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                log.error("[" + configList[i] + "] 初始化失败", ex);
                throw new BeanCreationException("资源加载初始化失败 ");
            }
        }
        return res;
    }

    public static List<Field> findAllField(Class clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                res.add(field);
            }
        });
        return res;
    }

    public static List<Method> findAllMethod(Class clazz) {
        final List<Method> res = new LinkedList<>();
        ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                res.add(method);
            }
        });
        return res;
    }

}
