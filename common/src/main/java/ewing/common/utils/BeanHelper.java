package ewing.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class BeanHelper {

    private BeanHelper() {
    }

    /**
     * 调用对象中的方法。
     */
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
     * 复制集合对象属性到List。
     */
    public static <S extends Iterable<?>, T> List<T> copyForList(S sources, Supplier<T> targetCreator) {
        return copyForCollection(sources, targetCreator, ArrayList::new);
    }

    /**
     * 复制集合对象属性到集合。
     */
    public static <S extends Iterable<?>, C extends Collection<T>, T> C copyForCollection(S sources, Supplier<T> targetCreator, Supplier<C> collectionCreator) {
        if (sources == null || targetCreator == null || collectionCreator == null) {
            return null;
        }
        C collection = collectionCreator.get();
        if (collection == null) {
            return null;
        }
        for (Object source : sources) {
            collection.add(copyForBean(source, targetCreator));
        }
        return collection;
    }

    /**
     * 复制对象属性到对象。
     */
    public static <T> T copyForBean(Object source, Supplier<T> targetCreator) {
        if (source == null || targetCreator == null) {
            return null;
        }
        T target = targetCreator.get();
        if (target == null) {
            return null;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 复制集合对象同义属性到List。
     */
    public static <S extends Iterable<?>, T> List<T> copySynonymForList(S sources, Supplier<T> targets) {
        return copySynonymForCollection(sources, targets, ArrayList::new);
    }

    /**
     * 复制集合对象同义属性到集合。
     */
    public static <S extends Iterable<?>, C extends Collection<T>, T> C copySynonymForCollection(S sources, Supplier<T> targets, Supplier<C> collection) {
        if (sources == null || targets == null || collection == null) {
            return null;
        }
        C targetCollection = collection.get();
        if (targetCollection == null) {
            return null;
        }
        for (Object source : sources) {
            targetCollection.add(copySynonymForBean(source, targets));
        }
        return targetCollection;
    }

    /**
     * 复制对象同义属性到对象。
     */
    public static <T> T copySynonymForBean(Object source, Supplier<T> target) {
        return copySynonymFields(source, target.get());
    }

    private static final Map<SimpleEntry<Class, Class>, List<SimpleEntry<PropertyDescriptor, PropertyDescriptor>>> SYNONYM_MAP = new ConcurrentHashMap<>();

    /**
     * 相同含义字段名属性复制，即忽略字段名中的大小写和下划线。
     * <p>
     * 字段类型必须兼容，包装类型为null时不会复制到对应的基本类型。
     */
    public static <T> T copySynonymFields(Object source, T target) {
        if (source == null || target == null) {
            return target;
        }
        SimpleEntry<Class, Class> classEntry = new SimpleEntry<>(source.getClass(), target.getClass());
        List<SimpleEntry<PropertyDescriptor, PropertyDescriptor>> propertyEntries = SYNONYM_MAP.computeIfAbsent(classEntry, entry -> {
            try {
                PropertyDescriptor[] sourceProperties = Introspector.getBeanInfo(entry.getKey()).getPropertyDescriptors();
                PropertyDescriptor[] targetProperties = Introspector.getBeanInfo(entry.getValue()).getPropertyDescriptors();
                List<SimpleEntry<PropertyDescriptor, PropertyDescriptor>> entries = new ArrayList<>();
                for (PropertyDescriptor sourceProperty : sourceProperties) {
                    for (PropertyDescriptor targetProperty : targetProperties) {
                        if (sourceProperty.getReadMethod() != null && targetProperty.getWriteMethod() != null &&
                                ClassUtils.isAssignable(targetProperty.getPropertyType(), sourceProperty.getPropertyType()) &&
                                sourceProperty.getName().replace("_", "")
                                        .equalsIgnoreCase(targetProperty.getName().replace("_", ""))) {
                            entries.add(new SimpleEntry<>(sourceProperty, targetProperty));
                        }
                    }
                }
                return entries;
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        });
        try {
            for (SimpleEntry<PropertyDescriptor, PropertyDescriptor> propertyEntry : propertyEntries) {
                Object value = propertyEntry.getKey().getReadMethod().invoke(source);
                if (value != null || !propertyEntry.getValue().getPropertyType().isPrimitive()) {
                    propertyEntry.getValue().getWriteMethod().invoke(target, value);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return target;
    }

    /**
     * 通过序列化深度复制对象。
     */
    public static <T extends Serializable> T deepCopySerializable(T source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(source);
            return (T) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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
