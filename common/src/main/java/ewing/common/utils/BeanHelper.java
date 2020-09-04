package ewing.common.utils;

import org.springframework.util.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class BeanHelper {
    private static final Map<CopyCacheKey, List<PropertyPair>> COPY_CACHE = new ConcurrentHashMap<>();

    private BeanHelper() {
        throw new IllegalStateException("Can not construct BeanHelper");
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
    public static <E> E invokeSilence(Object target, String methodName, Class<?>[] argTypes, Object... args) {
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
        return copyProperties(source, targetCreator.get());
    }

    /**
     * 复制对象属性，比Spring的BeanUtils快且具有更好的兼容性。
     */
    public static <T> T copyProperties(Object source, T target) {
        if (source == null || target == null) {
            return target;
        }
        List<PropertyPair> propertyPairs = getCachedPropertyPairs(source, target, false);
        copyByPropertyPairs(source, target, propertyPairs);
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
    public static <T> T copySynonymForBean(Object source, Supplier<T> targetCreator) {
        if (source == null || targetCreator == null) {
            return null;
        }
        return copySynonymProperties(source, targetCreator.get());
    }

    /**
     * 相同含义字段名属性复制，即忽略字段名中的大小写和下划线。
     * <p>
     * 字段类型必须兼容，包装类型为null时不会复制到对应的基本类型。
     */
    public static <T> T copySynonymProperties(Object source, T target) {
        if (source == null || target == null) {
            return target;
        }
        List<PropertyPair> propertyPairs = getCachedPropertyPairs(source, target, true);
        copyByPropertyPairs(source, target, propertyPairs);
        return target;
    }

    /**
     * 通过属性对复制属性。
     */
    private static <T> void copyByPropertyPairs(Object source, T target, List<PropertyPair> propertyPairs) {
        try {
            for (PropertyPair propertyPair : propertyPairs) {
                Method readMethod = propertyPair.sourceProperty.getReadMethod();
                if (!propertyPair.sourceAccessible) {
                    readMethod.setAccessible(true);
                }
                Object value = readMethod.invoke(source);
                if (value != null || !propertyPair.targetProperty.getPropertyType().isPrimitive()) {
                    Method writeMethod = propertyPair.targetProperty.getWriteMethod();
                    if (!propertyPair.targetAccessible) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(target, value);
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取待复制对象的属性对。
     */
    private static <T> List<PropertyPair> getCachedPropertyPairs(Object source, T target, boolean synonym) {
        CopyCacheKey cacheKey = new CopyCacheKey(source.getClass(), target.getClass(), synonym);
        return COPY_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                PropertyDescriptor[] sourceProperties = Introspector.getBeanInfo(key.sourceClass).getPropertyDescriptors();
                PropertyDescriptor[] targetProperties = Introspector.getBeanInfo(key.targetClass).getPropertyDescriptors();
                List<PropertyPair> propertyPairs = new ArrayList<>();
                for (PropertyDescriptor targetProperty : targetProperties) {
                    for (PropertyDescriptor sourceProperty : sourceProperties) {
                        Method readMethod = sourceProperty.getReadMethod();
                        Method writeMethod = targetProperty.getWriteMethod();
                        if (readMethod != null && writeMethod != null &&
                                ClassUtils.isAssignable(targetProperty.getPropertyType(), sourceProperty.getPropertyType())) {
                            // 名称相同或者名称同义则添加到属性对
                            if (sourceProperty.getName().equals(targetProperty.getName()) ||
                                    (key.synonym && sourceProperty.getName().replace("_", "")
                                            .equalsIgnoreCase(targetProperty.getName().replace("_", "")))) {
                                propertyPairs.add(new PropertyPair(sourceProperty, targetProperty,
                                        Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()),
                                        Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())));
                                break;
                            }
                        }
                    }
                }
                return propertyPairs;
            } catch (IntrospectionException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static class CopyCacheKey {
        private final Class<?> sourceClass;
        private final Class<?> targetClass;
        private final boolean synonym;

        private CopyCacheKey(Class<?> sourceClass, Class<?> targetClass, boolean synonym) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
            this.synonym = synonym;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CopyCacheKey that = (CopyCacheKey) o;
            return synonym == that.synonym &&
                    sourceClass.equals(that.sourceClass) &&
                    targetClass.equals(that.targetClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceClass, targetClass, synonym);
        }
    }

    private static class PropertyPair {
        private final PropertyDescriptor sourceProperty;
        private final PropertyDescriptor targetProperty;
        private final boolean sourceAccessible;
        private final boolean targetAccessible;

        public PropertyPair(PropertyDescriptor sourceProperty, PropertyDescriptor targetProperty, boolean sourceAccessible, boolean targetAccessible) {
            this.sourceProperty = sourceProperty;
            this.targetProperty = targetProperty;
            this.sourceAccessible = sourceAccessible;
            this.targetAccessible = targetAccessible;
        }
    }

}
