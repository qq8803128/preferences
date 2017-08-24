package org.ollyice.preferences;

import org.ollyice.preferences.annotation.CHANGED;
import org.ollyice.preferences.annotation.READ;
import org.ollyice.preferences.annotation.WRITE;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/24.
 */

final class Utils implements TypeWrapper {
    static Map<String, TypeInfo> getWriteProxyMethodsReturnType(Method[] methods) {
        Map<String, TypeInfo> map = new HashMap<>();
        for (Method method : methods) {
            WRITE write = method.getAnnotation(WRITE.class);
            if (write != null) {
                map.put(write.value(), new TypeInfo(write.value(),getMethodType(method), write.sync()));
            }
        }
        return map;
    }

    static Map<String,String> getReadProxyMethods(Method[] methods){
        Map<String,String> map = new HashMap<>();
        for (Method method : methods) {
            READ read = method.getAnnotation(READ.class);
            if (read != null){
                map.put(method.getName(),read.value());
            }
        }
        return map;
    }

    static Map<String,String> getWriteProxyMethods(Method[] methods){
        Map<String,String> map = new HashMap<>();
        for (Method method : methods) {
            WRITE write = method.getAnnotation(WRITE.class);
            if (write != null){
                map.put(method.getName(),write.value());
            }
        }
        return map;
    }

    static int getMethodType(Method method) {
        Type type = method.getGenericParameterTypes()[0];
        if (type == String.class) {
            return TYPE_STRING;
        } else if (type == Set.class) {
            return TYPE_SET;
        } else if (type == Integer.class || type == int.class) {
            return TYPE_INT;
        } else if (type == Long.class || type == long.class) {
            return TYPE_LONG;
        } else if (type == Float.class || type == float.class) {
            return TYPE_FLOAT;
        } else if (type == Boolean.class || type == boolean.class) {
            return TYPE_BOOLEAN;
        }
        return TYPE_UNKNOWN;
    }

    public static Map<String, String> getOnValueChangeMethods(Method[] methods) {
        Map<String,String> map = new HashMap<>();
        for (Method method : methods) {
            CHANGED write = method.getAnnotation(CHANGED.class);
            if (write != null){
                map.put(method.getName(),write.value());
            }
        }
        return map;
    }

    public static List<String> getWrapperMethods(Method[] methods) {
        List<String> list = new ArrayList<>();
        for (Method method : methods) {
            list.add(method.getName());
        }
        return list;
    }

    public static Map<String, String> getOnValueKeyChangeMethods(Method[] methods) {
        Map<String,String> map = new HashMap<>();
        for (Method method : methods) {
            CHANGED write = method.getAnnotation(CHANGED.class);
            if (write != null){
                map.put(write.value(),method.getName());
            }
        }
        return map;
    }
}
