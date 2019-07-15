package ewing.common.utils;

import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Objects;
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
     * 生成实体类属性转换器。
     */
    public static void generateCopier(Class<?> source, Class<?> target) {
        if (source == null || target == null) {
            return;
        }

        URL resource = Objects.requireNonNull(target.getClassLoader().getResource(""));
        String parent = new File(resource.getPath()).getParentFile().getParent();
        String className = source.getSimpleName() + "To" + target.getSimpleName();

        String path = parent + File.separator +
                "src" + File.separator +
                "main" + File.separator +
                "java" + File.separator +
                target.getPackage().getName().replace(".", File.separator) + File.separator +
                className + ".java";

        File file = new File(path);
        System.out.println("Generate code：");
        System.out.println();

        StringBuilder content = new StringBuilder();
        content.append("package ").append(target.getPackage().getName()).append(";");
        content.append("\n");
        content.append("\nimport ").append(source.getName()).append(";");
        content.append("\nimport ").append(target.getName()).append(";");
        content.append("\n");
        content.append("\npublic class ").append(className).append(" {");
        content.append("\n");
        content.append("\n    public static ").append(target.getSimpleName()).append(" copy(")
                .append(source.getSimpleName()).append(" source, ").append(target.getSimpleName()).append(" target){");
        content.append("\n        if(source == null || target == null)");
        content.append("\n            return target;");

        try {
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(source);
            BeanInfo targetBeanInfo = Introspector.getBeanInfo(target);

            Stream.of(targetBeanInfo.getPropertyDescriptors())
                    .filter(tpd -> tpd.getReadMethod() != null && tpd.getWriteMethod() != null)
                    .forEach(tpd -> {
                        Optional<PropertyDescriptor> opd = Stream.of(sourceBeanInfo.getPropertyDescriptors())
                                .filter(pd -> pd.getPropertyType().equals(tpd.getPropertyType()))
                                .filter(pd -> tpd.getName().replaceAll("[_$]", "")
                                        .equalsIgnoreCase(pd.getName().replaceAll("[_$]", "")))
                                .filter(pd -> ClassUtils.isAssignable(tpd.getPropertyType(), pd.getPropertyType()))
                                .findFirst();
                        opd.ifPresent(pd -> content.append("\n        target.").append(tpd.getWriteMethod().getName())
                                .append("(").append("source.").append(pd.getReadMethod().getName()).append("());"));
                    });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        content.append("\n        return target;");
        content.append("\n    }");
        content.append("\n");
        content.append("\n}");

        String fileContent = content.toString();
        System.out.println(fileContent);
        System.out.println();
        try {
            FileCopyUtils.copy(fileContent, new FileWriter(file));
            System.out.println("Generate file：");
            System.out.println(file.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
