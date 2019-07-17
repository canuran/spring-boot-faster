package ewing.common.utils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

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
     * 给单测用，初始化对象中不为空的简单属性。
     */
    public static <T> T initSimpleFields(T object) {
        // 存放类型生成的值以防止循环生成
        Map<Class, Object> context = new HashMap<>();
        return (T) initSimpleFields(object, context);
    }

    /**
     * 给单测用，创建对象并初始化对象中不为空的简单属性。
     */
    public static <T> T generateInstance(Type type) {
        // 存放类型生成的值以防止循环生成
        Map<Class, Object> context = new HashMap<>();
        return (T) generateInstance(type, context);
    }

    private static Object initSimpleFields(Object object, Map<Class, Object> context) {
        if (object == null) return null;
        Class cls = object.getClass();
        while (cls != null && !cls.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (Modifier.isFinal(field.getModifiers()) || field.get(object) != null)
                        continue;
                    Object value = generateInstance(field.getGenericType(), context);
                    if (value != null)
                        field.set(object, value);
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
            cls = cls.getSuperclass();
        }
        return object;
    }

    private static final Map<Class, Object> CONST_TYPE_VALUES = new HashMap<>();

    static {
        long time = System.currentTimeMillis();
        CONST_TYPE_VALUES.put(boolean.class, true);
        CONST_TYPE_VALUES.put(char.class, '1');
        CONST_TYPE_VALUES.put(byte.class, (byte) 1);
        CONST_TYPE_VALUES.put(short.class, (short) 1);
        CONST_TYPE_VALUES.put(int.class, 1);
        CONST_TYPE_VALUES.put(long.class, 1L);
        CONST_TYPE_VALUES.put(float.class, 1F);
        CONST_TYPE_VALUES.put(double.class, 1D);
        CONST_TYPE_VALUES.put(Boolean.class, true);
        CONST_TYPE_VALUES.put(Character.class, '1');
        CONST_TYPE_VALUES.put(Byte.class, (byte) 1);
        CONST_TYPE_VALUES.put(Short.class, (short) 1);
        CONST_TYPE_VALUES.put(Integer.class, 1);
        CONST_TYPE_VALUES.put(Long.class, 1L);
        CONST_TYPE_VALUES.put(Float.class, 1F);
        CONST_TYPE_VALUES.put(Double.class, 1D);
        CONST_TYPE_VALUES.put(String.class, "1");
        CONST_TYPE_VALUES.put(BigDecimal.class, BigDecimal.ONE);
        CONST_TYPE_VALUES.put(BigInteger.class, BigInteger.ONE);
        CONST_TYPE_VALUES.put(Date.class, new Date(time));
        CONST_TYPE_VALUES.put(Time.class, new Time(time));
        CONST_TYPE_VALUES.put(Timestamp.class, new Timestamp(time));
    }

    private static Object generateInstance(Type type, Map<Class, Object> context) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType == Collection.class || rawType == List.class || rawType == LinkedList.class) {
                Object value = getSingleValue(parameterizedType.getActualTypeArguments(), context);
                List<Object> objects = new LinkedList();
                if (value != null) objects.add(value);
                return objects;
            } else if (rawType == Set.class || rawType == HashSet.class) {
                Object value = getSingleValue(parameterizedType.getActualTypeArguments(), context);
                HashSet<Object> objects = new HashSet<>();
                if (value != null) objects.add(value);
                return objects;
            } else if (rawType == SortedSet.class || rawType == TreeSet.class) {
                Object value = getSingleValue(parameterizedType.getActualTypeArguments(), context);
                TreeSet<Object> objects = new TreeSet<>();
                if (value != null) objects.add(value);
                return objects;
            } else if (rawType == LinkedHashSet.class) {
                Object value = getSingleValue(parameterizedType.getActualTypeArguments(), context);
                LinkedHashSet<Object> objects = new LinkedHashSet<>();
                if (value != null) objects.add(value);
                return objects;
            } else if (rawType == ArrayList.class) {
                Object value = getSingleValue(parameterizedType.getActualTypeArguments(), context);
                ArrayList<Object> objects = new ArrayList<>();
                if (value != null) objects.add(value);
                return objects;
            } else if (rawType == Map.class || rawType == HashMap.class) {
                AbstractMap.SimpleEntry entry = getPairValue(parameterizedType.getActualTypeArguments(), context);
                Map map = new HashMap();
                if (entry != null) map.put(entry.getKey(), entry.getValue());
                return map;
            } else if (rawType == SortedMap.class || rawType == TreeMap.class) {
                AbstractMap.SimpleEntry entry = getPairValue(parameterizedType.getActualTypeArguments(), context);
                Map map = new TreeMap();
                if (entry != null) map.put(entry.getKey(), entry.getValue());
                return map;
            } else if (rawType == LinkedHashMap.class) {
                AbstractMap.SimpleEntry entry = getPairValue(parameterizedType.getActualTypeArguments(), context);
                Map map = new LinkedHashMap();
                if (entry != null) map.put(entry.getKey(), entry.getValue());
                return map;
            } else {
                return generateInstance(rawType, context);
            }
        } else if (type instanceof Class) {
            Object value = CONST_TYPE_VALUES.get(type);
            if (value == null) {
                value = context.get(type);
                if (value == null)
                    try {
                        Class clazz = (Class) type;
                        value = clazz.isEnum() ? clazz.getEnumConstants()[0] : clazz.newInstance();
                        if (value != null) {
                            initSimpleFields(value, context);
                            context.put(clazz, value);
                        }
                    } catch (Exception e) {
                        return null;
                    }
            }
            return value;
        }
        return null;
    }

    private static AbstractMap.SimpleEntry getPairValue(Type[] types, Map<Class, Object> context) {
        if (types == null || types.length < 2) {
            return null;
        } else {
            return new AbstractMap.SimpleEntry(generateInstance(types[0], context),
                    generateInstance(types[1], context));
        }
    }

    private static Object getSingleValue(Type[] types, Map<Class, Object> context) {
        if (types == null || types.length == 0) {
            return null;
        } else {
            return generateInstance(types[0], context);
        }
    }

}
