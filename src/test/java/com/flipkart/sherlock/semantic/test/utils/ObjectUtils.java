package com.flipkart.sherlock.semantic.test.utils;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by dhruv.pancholi on 14/10/17.
 */
public class ObjectUtils {

    public static boolean isAnyFieldNull(Object inputObject) {
        Field[] fields = inputObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object o = field.get(inputObject);
                if (o == null) return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }

    public static List<String> getFieldNames(Object inputObject) {
        Field[] fields = inputObject.getClass().getDeclaredFields();
        List<String> names = new ArrayList<>();
        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            names.add(field.getName());
        }
        return names;
    }

    public static void invokeGetters(Set<String> fields, Object inputObject) {
        for (String fieldName : fields) {
            String methodName = "get" + StringUtils.capitalize(fieldName);
            try {
                Method method = inputObject.getClass().getMethod(methodName);
                method.invoke(inputObject);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean containsSetters(Object inputObject) {
        Field[] fields = inputObject.getClass().getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String methodName = "set" + StringUtils.capitalize(fieldName);
            try {
                Method method = inputObject.getClass().getMethod(methodName);
                return true;
            } catch (NoSuchMethodException ignored) {

            }
        }
        return false;
    }

    public static boolean areAllFieldsPrivate(Object inputObject) {
        Field[] fields = inputObject.getClass().getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (Modifier.isPublic(field.getModifiers())) return false;

        }
        return true;
    }
}
