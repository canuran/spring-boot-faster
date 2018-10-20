package ewing.faster;

import com.querydsl.maven.AbstractMetaDataExportMojo;
import com.querydsl.maven.MetadataExportMojo;
import com.querydsl.sql.codegen.support.NumericMapping;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Field;
import java.util.stream.IntStream;

/**
 * 自动生成Dao接口与实现，比Maven配置更灵活。
 *
 * @author Ewing
 * @since 2018年05月21日
 */
public class GenerateQuery {

    /***
     * 生成Bean和QBean。
     */
    public static void main(String[] args) throws Exception {
        // 配置参数并导出代码
        MetadataExportMojo exporter = new MetadataExportMojo();
        MavenProject project = new MavenProject();
        project.getProperties().setProperty("project.build.sourceEncoding", "UTF-8");
        exporter.setProject(project);
        exporter.setJdbcUrl("jdbc:mysql://localhost:3306/faster?useUnicode=true&characterEncoding=UTF-8");
        exporter.setJdbcDriver("com.mysql.jdbc.Driver");
        exporter.setJdbcUser("faster");
        exporter.setJdbcPassword("faster");
        exporter.setExportBeans(true);
        exporter.setNamePrefix("Q");
        exporter.setTargetFolder("faster/src/main/java");
        exporter.setPackageName("ewing.faster.dao.query");
        exporter.setBeanPackageName("ewing.faster.dao.entity");
        exporter.setBeanInterfaces(new String[]{java.io.Serializable.class.getName()});
        Field beanAddToString = AbstractMetaDataExportMojo.class.getDeclaredField("beanAddToString");
        beanAddToString.setAccessible(true);
        beanAddToString.set(exporter, true);
        Field exportPrimaryKeys = AbstractMetaDataExportMojo.class.getDeclaredField("exportPrimaryKeys");
        exportPrimaryKeys.setAccessible(true);
        exportPrimaryKeys.set(exporter, true);
        exporter.setCustomTypes(new String[]{com.querydsl.sql.types.UtilDateType.class.getName()});
        exporter.setNumericMappings(getNumericMappings());
        exporter.setTableNamePattern("%");
        exporter.execute();
    }

    /**
     * 10位以下的整数统一使用Integer。
     */
    private static NumericMapping[] getNumericMappings() {
        return IntStream.range(1, 10).mapToObj(i -> {
            NumericMapping mapping = new NumericMapping();
            mapping.setDecimal(0);
            mapping.setTotal(i);
            mapping.setJavaType(Integer.class.getName());
            return mapping;
        }).toArray(NumericMapping[]::new);
    }

}
