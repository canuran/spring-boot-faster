package ewing.common.utils;

import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class BeanHelper {

    private BeanHelper() {
    }

    /**
     * 调用对象中的方法。
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethod(Object target, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);
            return (E) method.invoke(target, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Invoke method " + methodName + " failed: " + e.getMessage(), e);
        }
    }

    /**
     * 静默调用对象中的方法。
     */
    public static <E> E invokeSlience(Object target, String methodName, Class<?>[] argTypes, Object... args) {
        try {
            return invokeMethod(target, methodName, argTypes, args);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 主要给单测用，填充对象中的简单的null属性。
     */
    public static <T> T setSimpleNullFields(T object) {
        if (object == null) return null;
        Class cls = object.getClass();
        while (cls != null && !cls.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.get(object) != null) continue;
                    Object value;
                    if (field.getType().isAssignableFrom(String.class)) {
                        value = "1";
                    } else if (field.getType().isAssignableFrom(BigDecimal.class)) {
                        value = BigDecimal.ONE;
                    } else if (field.getType().isAssignableFrom(BigInteger.class)) {
                        value = BigInteger.ONE;
                    } else if (field.getType().isAssignableFrom(Timestamp.class)) {
                        value = new Timestamp(System.currentTimeMillis());
                    } else if (field.getType().isAssignableFrom(Time.class)) {
                        value = new Time(System.currentTimeMillis());
                    } else if (field.getType().isAssignableFrom(Date.class)) {
                        value = new Date();
                    } else if (ClassUtils.isAssignable(field.getType(), int.class)) {
                        value = 1;
                    } else if (ClassUtils.isAssignable(field.getType(), long.class)) {
                        value = 1L;
                    } else if (ClassUtils.isAssignable(field.getType(), float.class)) {
                        value = 1.0F;
                    } else if (ClassUtils.isAssignable(field.getType(), double.class)) {
                        value = 1.0D;
                    } else if (ClassUtils.isAssignable(field.getType(), boolean.class)) {
                        value = false;
                    } else if (ClassUtils.isAssignable(field.getType(), char.class)) {
                        value = '1';
                    } else if (ClassUtils.isAssignable(field.getType(), short.class)) {
                        value = (short) 1;
                    } else if (ClassUtils.isAssignable(field.getType(), byte.class)) {
                        value = (byte) 1;
                    } else {
                        continue;
                    }
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            cls = cls.getSuperclass();
        }
        return object;
    }

}
