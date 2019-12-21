package ewing.common.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

/**
 * 请输入描述说明。
 *
 * @author caiyouyuan
 * @since 2019年12月21日
 */
public class BeanTestUtils {
    private BeanTestUtils() {
        throw new IllegalStateException("Can not construct BeanTestUtils");
    }

    /**
     * 给单测用，初始化对象中不为空的简单属性。
     */
    public static <T> T initSimpleFields(T object) {
        // 存放类型生成过的类型
        Set<Type> context = new HashSet<>();
        return (T) initSimpleFields(object, context);
    }

    /**
     * 给单测用，创建对象并初始化对象中不为空的简单属性。
     */
    public static <T> T generateInstance(Type type) {
        // 存放类型生成过的类型
        Set<Type> context = new HashSet<>();
        return (T) generateInstance(type, context);
    }

    private static Object initSimpleFields(Object object, Set<Type> context) {
        if (object == null) return null;
        Class cls = object.getClass();
        while (cls != null && !cls.equals(Object.class)) {
            try {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (Modifier.isFinal(field.getModifiers()) || field.get(object) != null)
                        continue;
                    Object value = generateInstance(field.getGenericType(), context);
                    if (value != null)
                        field.set(object, value);
                }
            } catch (Throwable e) {
                System.out.println("Init " + object + " fail:" + e.getMessage());
            }
            cls = cls.getSuperclass();
        }
        return object;
    }

    private static final Map<Type, Object> CONST_TYPE_VALUES = new HashMap<>();

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
        CONST_TYPE_VALUES.put(java.sql.Date.class, new java.sql.Date(time));
        CONST_TYPE_VALUES.put(Time.class, new Time(time));
        CONST_TYPE_VALUES.put(Timestamp.class, new Timestamp(time));
    }

    private static Object generateInstance(Type type, Set<Type> context) {
        if (type == null) return null;
        // 常量池有则直接返回常量
        Object result = CONST_TYPE_VALUES.get(type);
        if (result != null) return result;

        if (type instanceof ParameterizedType) {
            // 处理泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (rawType == Collection.class || rawType == List.class || rawType == LinkedList.class) {
                return resolveCollection(new LinkedList(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == Set.class || rawType == HashSet.class) {
                return resolveCollection(new HashSet(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == SortedSet.class || rawType == TreeSet.class) {
                return resolveCollection(new TreeSet(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == LinkedHashSet.class) {
                return resolveCollection(new LinkedHashSet(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == ArrayList.class) {
                return resolveCollection(new ArrayList(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == Map.class || rawType == HashMap.class) {
                return resolveMapValue(new HashMap(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == SortedMap.class || rawType == TreeMap.class) {
                return resolveMapValue(new TreeMap(), parameterizedType.getActualTypeArguments(), context);
            } else if (rawType == LinkedHashMap.class) {
                return resolveMapValue(new LinkedHashMap(), parameterizedType.getActualTypeArguments(), context);
            } else {
                result = generateInstance(rawType, context);
            }
        } else if (type instanceof Class) {
            // 避免循环生成所以只能生成一次
            if (context.contains(type)) return null;
            context.add(type);
            try {
                Class clazz = (Class) type;
                result = clazz.isEnum() ? clazz.getEnumConstants()[0] : clazz.newInstance();
                if (result != null)
                    initSimpleFields(result, context);
            } catch (Throwable e) {
                return null;
            }
        }
        return result;
    }

    private static <T extends Collection> T resolveCollection(T coll, Type[] types, Set<Type> context) {
        if (types == null || types.length < 1) {
            return coll;
        } else {
            Object object = generateInstance(types[0], context);
            if (object != null) {
                coll.add(object);
            }
            return coll;
        }
    }

    private static <T extends Map> T resolveMapValue(T map, Type[] types, Set<Type> context) {
        if (types == null || types.length < 2) {
            return map;
        } else {
            Object key = generateInstance(types[0], context);
            Object object = generateInstance(types[1], context);
            if (key != null && object != null) {
                map.put(key, object);
            }
            return map;
        }
    }


}
