package ewing.common.utils;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

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
     * 获取集合中唯一的元素。
     */
    public static <C extends Collection<E>, E> E getUnique(C source) {
        Assert.notEmpty(source, "集合不能为空！");
        Assert.isTrue(source.size() == 1, "集合元素不唯一！");
        return source.iterator().next();
    }

    /**
     * 获取集合中第一个元素。
     */
    public static <C extends Collection<E>, E> E getFirst(C source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        } else {
            return source.iterator().next();
        }
    }

    /**
     * 转换代码生成器，属性的字母数字以及类型匹配上即可。
     */
    public static void genConvertCodeUseBuilder(Class<?> from, Class<?> to, String fromArgName) {
        if (from == null || to == null) {
            return;
        }

        try {
            BeanInfo fromBeanInfo = Introspector.getBeanInfo(from);
            BeanInfo toBeanInfo = Introspector.getBeanInfo(to);

            System.out.println(to.getSimpleName() + ".builder()");

            Stream.of(toBeanInfo.getPropertyDescriptors())
                    .filter(fpd -> !fpd.getName().equals("class"))
                    .forEach(fpd -> {
                        Optional<PropertyDescriptor> opd = Stream.of(fromBeanInfo.getPropertyDescriptors())
                                .filter(pd -> pd.getPropertyType().equals(fpd.getPropertyType()) &&
                                        fpd.getName().replaceAll("[^a-zA-Z0-9]", "")
                                                .equalsIgnoreCase(pd.getName().replaceAll("[^a-zA-Z0-9]", "")))
                                .findFirst();
                        opd.ifPresent(pd -> System.out.println(
                                "." + fpd.getName() + "(" + fromArgName + "." + pd.getReadMethod().getName() + "())"));
                        if (!opd.isPresent()) {
                            System.out.println("." + fpd.getName() + "(null)");
                        }
                    });
            System.out.println(".build();");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 转换代码生成器，属性的字母数字以及类型匹配上即可。
     */
    public static void genConvertCodeUseGetter(Class<?> from, Class<?> to, String fromArgName) {
        if (from == null || to == null) {
            return;
        }

        try {
            BeanInfo fromBeanInfo = Introspector.getBeanInfo(from);
            BeanInfo toBeanInfo = Introspector.getBeanInfo(to);

            String varName = StringUtils.uncapitalize(to.getSimpleName());
            System.out.println(to.getSimpleName() + " " + varName + " = new " + to.getSimpleName() + "();");

            Stream.of(toBeanInfo.getPropertyDescriptors())
                    .filter(fpd -> !fpd.getName().equals("class"))
                    .forEach(fpd -> {
                        Optional<PropertyDescriptor> opd = Stream.of(fromBeanInfo.getPropertyDescriptors())
                                .filter(pd -> pd.getPropertyType().equals(fpd.getPropertyType()) &&
                                        fpd.getName().replaceAll("[^a-zA-Z0-9]", "")
                                                .equalsIgnoreCase(pd.getName().replaceAll("[^a-zA-Z0-9]", "")))
                                .findFirst();
                        opd.ifPresent(pd -> System.out.println(
                                varName + "." + fpd.getWriteMethod().getName() + "(" +
                                        fromArgName + "." + pd.getReadMethod().getName() + "());"));
                        if (!opd.isPresent()) {
                            System.out.println(varName + "." + fpd.getWriteMethod().getName() + "(null);");
                        }
                    });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
