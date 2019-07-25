package ewing.common.utils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象属性复制工具类，生成赋值代码编译运行，和手动编写代码一样快。
 *
 * @author caiyouyuan
 * @since 2019年07月16日
 */
public class BeanCopy {

    private static final ClassPool CLASS_POOL = ClassPool.getDefault();

    private static final Map<CopierKey, Copier> COPIER_MAP = new ConcurrentHashMap<>(32);

    /**
     * 复制对象属性，字段名称必须一致。
     * <p>
     * 字段类型必须兼容目标字段类型，包装类型和基本类型之间不会复制。
     */
    public static <T> T copyProperties(Object source, T target) {
        return copyProperties(source, target, false);
    }

    /**
     * 复制字段名含义相同的属性，即忽略字段名中的大小写和下划线。
     * <p>
     * 字段类型必须兼容目标字段类型，包装类型和基本类型之间不会复制。
     */
    public static <T> T copySynonyms(Object source, T target) {
        return copyProperties(source, target, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> T copyProperties(Object source, T target, boolean synonym) {
        if (source == null || target == null) {
            return target;
        }

        CopierKey copierKey = new CopierKey(source.getClass(), target.getClass());
        Copier copier = COPIER_MAP.computeIfAbsent(copierKey, key -> {
            String copyCode = generateCopyCode(key);
            try {
                CtClass copierClass = CLASS_POOL.get(Copier.class.getName());
                CtClass copyClass = CLASS_POOL.makeClass(key.toString());
                copyClass.addInterface(copierClass);
                CtMethod ctMethod = CtMethod.make(copyCode, copyClass);
                copyClass.addMethod(ctMethod);
                return (Copier) copyClass.toClass().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        return (T) copier.copy(source, target, synonym);
    }

    private static String generateCopyCode(CopierKey copierKey) {
        if (copierKey.source == null || copierKey.target == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        content.append("public Object copy(Object source, Object target, boolean synonym) {")
                .append("\nif (source == null || target == null)")
                .append("\nreturn target;")
                .append("\n").append(copierKey.source.getName()).append(" src = (")
                .append(copierKey.source.getName()).append(") source;")
                .append("\n").append(copierKey.target.getName()).append(" tar = (")
                .append(copierKey.target.getName()).append(") target;")
                .append("\nif (synonym) {");

        StringBuilder sameBuilder = new StringBuilder();
        try {
            BeanInfo sourceBeanInfo = Introspector.getBeanInfo(copierKey.source);
            BeanInfo targetBeanInfo = Introspector.getBeanInfo(copierKey.target);

            for (PropertyDescriptor tpd : targetBeanInfo.getPropertyDescriptors()) {
                for (PropertyDescriptor spd : sourceBeanInfo.getPropertyDescriptors()) {
                    if (spd.getReadMethod() != null && tpd.getWriteMethod() != null
                            && tpd.getPropertyType().isAssignableFrom(spd.getPropertyType())) {
                        // 同义属性复制
                        if (tpd.getName().replace("_", "")
                                .equalsIgnoreCase(spd.getName().replace("_", ""))) {
                            content.append("\ntar.").append(tpd.getWriteMethod().getName())
                                    .append("(").append("src.").append(spd.getReadMethod().getName()).append("());");
                        }
                        // 相同属性复制
                        if (tpd.getName().equals(spd.getName())) {
                            sameBuilder.append("\ntar.").append(tpd.getWriteMethod().getName())
                                    .append("(").append("src.").append(spd.getReadMethod().getName()).append("());");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        content.append("\n} else {")
                .append(sameBuilder)
                .append("\n}")
                .append("\nreturn target;")
                .append("\n}");
        return content.toString();
    }

    public interface Copier {
        Object copy(Object source, Object target, boolean synonym);
    }

    private static class CopierKey {
        final Class source;
        final Class target;

        CopierKey(Class source, Class target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public String toString() {
            return "copier." + source.getName() + "." + target.getName();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CopierKey)) return false;
            CopierKey copierKey = (CopierKey) o;
            return Objects.equals(source, copierKey.source) &&
                    Objects.equals(target, copierKey.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }

}
