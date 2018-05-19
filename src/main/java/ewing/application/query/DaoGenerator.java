package ewing.application.query;

import com.querydsl.sql.RelationalPathBase;
import org.springframework.util.Assert;

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
 * 使用QBean生成Dao接口和实现类。
 */
public class DaoGenerator {

    public DaoGenerator() {
    }

    public void generate() {
        List<Class<?>> classes = getQueryBeanClasses();

        System.out.println("\nStart generating...\n");

        for (Class<?> queryBeanClass : classes) {
            Type superclass = queryBeanClass.getGenericSuperclass();
            Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
            Class beanClass = (Class) types[0];
            getGeneratedCode(INTERFACE_TEMPLATE, queryBeanClass, beanClass);
            getGeneratedCode(IMPLEMENTS_TEMPLATE, queryBeanClass, beanClass);
        }

        System.out.println("\nGenerating completed.");
    }


    private void getGeneratedCode(String template, Class<?> queryBeanClass, Class beanClass) {
        String code = template.replace("{daoPackage}", this.daoPackage)
                .replace("{daoSuperClass}", this.daoSuperClass.getName())
                .replace("{daoSuperClassName}", this.daoSuperClass.getSimpleName())
                .replace("{daoSuperInterface}", daoSuperInterface.getName())
                .replace("{daoSuperInterfaceName}", daoSuperInterface.getSimpleName())
                .replace("{beanClass}", beanClass.getName())
                .replace("{beanClassName}", beanClass.getSimpleName())
                .replace("{queryBeanClass}", queryBeanClass.getName())
                .replace("{queryBeanClassName}", queryBeanClass.getSimpleName());
        String codeFilePath = getPackageDirectory(this.daoPackage).getAbsolutePath();
        if (template.equals(INTERFACE_TEMPLATE)) {
            File directory = new File(codeFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            codeFilePath += FILE_SEPARATOR + beanClass.getSimpleName() + "Dao.java";
        } else {
            codeFilePath += FILE_SEPARATOR + "impl";
            File directory = new File(codeFilePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            codeFilePath += FILE_SEPARATOR + beanClass.getSimpleName() + "DaoImpl.java";
        }
        try {
            File file = new File(codeFilePath);
            if (!file.exists()) {
                file.createNewFile();
            } else if (!overwrrite) {
                return;
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(code);
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("Generated: " + file.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Class<?>> getQueryBeanClasses() {
        String absoluteJavaCodePath = new File(this.javaCodePath).getAbsolutePath();
        System.out.println("Absolute java code path: " + absoluteJavaCodePath + "\n");

        File queryBeanDirectory = getPackageDirectory(this.queryBeanPackage);
        Assert.isTrue(queryBeanDirectory.isDirectory(), "Invalid query bean path: " + queryBeanDirectory);
        System.out.println("QBean directory: " + queryBeanDirectory.getAbsolutePath() + "\n");

        List<Class<?>> classes = new ArrayList<>();
        for (File queryBeanFile : Objects.requireNonNull(queryBeanDirectory.listFiles())) {
            if (queryBeanFile.getName().endsWith(".java")) {
                try {
                    String absolutePath = queryBeanFile.getAbsolutePath();
                    String queryBeanClassName = absolutePath.substring(
                            absoluteJavaCodePath.length() + 1, absolutePath.length() - 5)
                            .replace(FILE_SEPARATOR, ".");

                    Class<?> clazz = Class.forName(queryBeanClassName);
                    if (RelationalPathBase.class.equals(clazz.getSuperclass())) {
                        classes.add(clazz);
                        System.out.println("Found QBean: " + queryBeanClassName);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    throw new RuntimeException(e);
                }
            }
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
            "public interface {beanClassName}Dao extends {daoSuperInterfaceName}<{beanClassName}> {\n" +
            "\n" +
            "}";

    private static final String IMPLEMENTS_TEMPLATE = "package {daoPackage}.impl;\n" +
            "\n" +
            "import {daoSuperClass};\n" +
            "import {beanClass};\n" +
            "import {queryBeanClass};\n" +
            "import {daoPackage}.{beanClassName}Dao;\n" +
            "import org.springframework.stereotype.Repository;\n" +
            "\n" +
            "@Repository\n" +
            "public class {beanClassName}DaoImpl extends {daoSuperClassName}<{queryBeanClassName}, {beanClassName}> implements {beanClassName}Dao {\n" +
            "\n" +
            "}";

    private String javaCodePath = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";
    private String queryBeanPackage = "query";
    private boolean overwrrite = false;
    private String daoPackage = "dao";
    private Class<?> daoSuperClass = BasisDao.class;
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


    public DaoGenerator overwrrite(boolean overwrrite) {
        this.overwrrite = overwrrite;
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
