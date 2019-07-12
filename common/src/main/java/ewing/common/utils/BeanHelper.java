package ewing.common.utils;

import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Pattern;
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

    private static final Pattern IGNORE_PATTERN = Pattern.compile("[_$]");

    /**
     * 相同含义字段名属性复制，即忽略字段名中的大小写、$和_，但字段类型必须兼容。
     * <p>
     * 该方法仅用于转换少于1000条的数据，比如返回给前端页面显示；追求效率时勿用。
     */
    public static <T> T copySynonymFields(Object source, T target) {
        if (source == null || target == null) return target;

        try {
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(source.getClass());
            BeanInfo targetBeanInfo = Introspector.getBeanInfo(target.getClass());

            for (PropertyDescriptor targetProperty : targetBeanInfo.getPropertyDescriptors()) {
                Method writeMethod = targetProperty.getWriteMethod();
                if (writeMethod == null || targetProperty.getReadMethod() == null)
                    continue;
                String targetPropertyName = targetProperty.getName();

                for (PropertyDescriptor sourceProperty : sourceBeanInfo.getPropertyDescriptors()) {
                    Method readMethod = sourceProperty.getReadMethod();
                    if (readMethod == null || sourceProperty.getWriteMethod() == null
                            || !targetProperty.getPropertyType().isAssignableFrom(sourceProperty.getPropertyType()))
                        continue;
                    String sourcePropertyName = sourceProperty.getName();

                    if (sourcePropertyName.equalsIgnoreCase(targetPropertyName)
                            || IGNORE_PATTERN.matcher(sourcePropertyName).replaceAll("")
                            .equalsIgnoreCase(IGNORE_PATTERN.matcher(targetPropertyName).replaceAll(""))) {
                        writeMethod.invoke(target, readMethod.invoke(source));
                        break;
                    }
                }
            }
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        return target;
    }

}
