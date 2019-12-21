package ewing.common.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class BeanHelper {
    private BeanHelper() {
        throw new IllegalStateException("Can not construct BeanHelper");
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

}
