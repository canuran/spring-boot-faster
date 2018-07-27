package ewing.query;

import com.mysema.commons.lang.Assert;
import com.querydsl.sql.RelationalPathBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 使用QBean生成Dao接口和实现类，先用Maven插件export后再执行Dao生成。
 */
public class DaoGenerator {

    public DaoGenerator() {
    }

    public void generate() {
        List<Class<?>> classes = getQueryBeanClasses();

        if (classes.isEmpty()) {
            return;
        } else {
            System.out.println("\nStart generating...\n");
        }

        int count = 0;
        for (Class<?> queryBeanClass : classes) {
            Type superclass = queryBeanClass.getGenericSuperclass();
            Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
            Class beanClass = (Class) types[0];
            count += getGeneratedCode(INTERFACE_TEMPLATE, queryBeanClass, beanClass);
            count += getGeneratedCode(IMPLEMENTS_TEMPLATE, queryBeanClass, beanClass);
        }
        if (count > 0) {
            System.out.println("\nGenerated files: " + count);
        } else {
            System.out.println("Generated no file, may exists and not overwrite.");
        }
    }

    private int getGeneratedCode(String template, Class<?> queryBeanClass, Class beanClass) {
        String beanSimpleName = beanClass.getSimpleName();
        String beanRealName = this.beanRemoveSuffix != null && !this.beanRemoveSuffix.isEmpty()
                && beanSimpleName.endsWith(this.beanRemoveSuffix) ?
                beanSimpleName.substring(0, beanSimpleName.lastIndexOf(this.beanRemoveSuffix)) : beanSimpleName;

        String code = template.replace("{daoPackage}", this.daoPackage)
                .replace("{daoSuperClass}", this.daoSuperClass.getName())
                .replace("{daoSuperSimpleName}", this.daoSuperClass.getSimpleName())
                .replace("{daoSuperInterface}", daoSuperInterface.getName())
                .replace("{daoSuperInterfaceName}", daoSuperInterface.getSimpleName())
                .replace("{beanClass}", beanClass.getName())
                .replace("{beanSimpleName}", beanSimpleName)
                .replace("{beanRealName}", beanRealName)
                .replace("{queryBeanClass}", queryBeanClass.getName())
                .replace("{queryBeanSimpleName}", queryBeanClass.getSimpleName());
        String codeFilePath = getPackageDirectory(this.daoPackage).getAbsolutePath();
        if (template.equals(INTERFACE_TEMPLATE)) {
            File directory = new File(codeFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            codeFilePath += FILE_SEPARATOR + beanRealName + "Dao.java";
        } else {
            codeFilePath += FILE_SEPARATOR + "impl";
            File directory = new File(codeFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            codeFilePath += FILE_SEPARATOR + beanRealName + "DaoImpl.java";
        }
        try {
            File file = new File(codeFilePath);
            if (!file.exists()) {
                file.createNewFile();
            } else if (!overwrite) {
                return 0;
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(code);
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("Generated: " + file.getName());
            return 1;
        } catch (IOException e) {
            System.out.println("Generate skipped because: " + e.getMessage());
            return 0;
        }
    }

    private List<Class<?>> getQueryBeanClasses() {
        String absoluteJavaCodePath = new File(this.javaCodePath).getAbsolutePath();
        System.out.println("Absolute java code path: " + absoluteJavaCodePath + "\n");

        File queryBeanDirectory = getPackageDirectory(this.queryBeanPackage);
        Assert.isTrue(queryBeanDirectory.isDirectory(), "Directory not exists of package: " + queryBeanPackage);
        System.out.println("QBean directory: " + queryBeanDirectory.getAbsolutePath() + "\n");

        List<Class<?>> classes = new ArrayList<>();
        for (File queryBeanFile : Objects.requireNonNull(queryBeanDirectory.listFiles())) {
            if (queryBeanFile.getName().endsWith(".java")) {
                try {
                    String absolutePath = queryBeanFile.getAbsolutePath();
                    String queryBeanSimpleName = absolutePath.substring(
                            absoluteJavaCodePath.length() + 1, absolutePath.length() - 5)
                            .replace(FILE_SEPARATOR, ".");

                    Class<?> clazz = Class.forName(queryBeanSimpleName);
                    if (RelationalPathBase.class.equals(clazz.getSuperclass())) {
                        classes.add(clazz);
                        System.out.println("Found QBean: " + queryBeanSimpleName);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (classes.isEmpty()) {
            System.out.println("Found no QBean in package: " + this.queryBeanPackage);
        }
        return classes;
    }

    private File getPackageDirectory(String packageName) {
        return new File(this.javaCodePath +
                (this.javaCodePath.endsWith(FILE_SEPARATOR) ? "" : FILE_SEPARATOR)
                + packageName.replace(".", FILE_SEPARATOR));
    }

    private static final String FILE_SEPARATOR = File.separator;

    private static final String INTERFACE_TEMPLATE = "package {daoPackage};\n" +
            "\n" +
            "import {daoSuperInterface};\n" +
            "import {beanClass};\n" +
            "\n" +
            "public interface {beanRealName}Dao extends {daoSuperInterfaceName}<{beanSimpleName}> {\n" +
            "\n" +
            "}";

    private static final String IMPLEMENTS_TEMPLATE = "package {daoPackage}.impl;\n" +
            "\n" +
            "import {daoSuperClass};\n" +
            "import {beanClass};\n" +
            "import {queryBeanClass};\n" +
            "import {daoPackage}.{beanRealName}Dao;\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "@Repository\n" +
            "public class {beanRealName}DaoImpl extends {daoSuperSimpleName}<{queryBeanSimpleName}, {beanSimpleName}> implements {beanRealName}Dao {\n" +
            "\n" +
            "}";

    private String javaCodePath = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";
    private String queryBeanPackage = "query";
    private String beanRemoveSuffix = "";
    private boolean overwrite = false;
    private String daoPackage = "dao";
    private Class<?> daoSuperClass = BasicDao.class;
    private Class<?> daoSuperInterface = BasicDao.class;

    public DaoGenerator javaCodePath(String javaCodePath) {
        Assert.hasText(javaCodePath, "Java code path should has text.");
        this.javaCodePath = javaCodePath;
        return this;
    }

    public DaoGenerator queryBeanPackage(String queryBeanPackage) {
        Assert.hasText(queryBeanPackage, "Query bean package should has text.");
        this.queryBeanPackage = queryBeanPackage;
        return this;
    }

    public DaoGenerator beanRemoveSuffix(String beanRemoveSuffix) {
        Assert.notNull(beanRemoveSuffix, "Bean remove suffix should not null.");
        this.beanRemoveSuffix = beanRemoveSuffix;
        return this;
    }

    public DaoGenerator overwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    public DaoGenerator daoPackage(String daoPackage) {
        Assert.hasText(daoPackage, "Dao package should has text.");
        this.daoPackage = daoPackage;
        return this;
    }

    public DaoGenerator daoSuperClass(Class<?> daoSuperClass) {
        Assert.notNull(daoSuperClass, "Dao super class should not null.");
        this.daoSuperClass = daoSuperClass;
        return this;
    }

    public DaoGenerator daoSuperInterface(Class<?> daoSuperInterface) {
        Assert.notNull(daoSuperInterface, "Dao super interface should not null.");
        this.daoSuperInterface = daoSuperInterface;
        return this;
    }

}
