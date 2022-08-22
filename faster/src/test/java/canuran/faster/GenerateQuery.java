package canuran.faster;

import com.querydsl.maven.MetadataExportMojo;
import com.querydsl.sql.codegen.support.NumericMapping;
import org.apache.maven.project.MavenProject;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.IntStream;

/**
 * 生成实体以及查询对象的工具类。
 *
 * @author canuran
 * @since 2018年05月21日
 */
public class GenerateQuery {

    /***
     * 生成实体以及查询对象，比Maven插件灵活些。
     */
    public static void main(String[] args) throws Exception {
        // 配置参数并导出代码
        MavenProject project = new MavenProject();
        project.getProperties().setProperty("project.build.sourceEncoding", "UTF-8");
        MetadataExportMojo exporter = new MetadataExportMojo();
        exporter.setProject(project);
        exporter.setJdbcUrl("jdbc:mysql://localhost:3306/faster?useUnicode=true&characterEncoding=UTF-8");
        exporter.setJdbcDriver("com.mysql.jdbc.Driver");
        exporter.setJdbcUser("faster");
        exporter.setJdbcPassword("faster");
        exporter.setExportBeans(true);
        exporter.setNamePrefix("Q");
        exporter.setTargetFolder("faster/src/main/java");
        exporter.setPackageName("canuran.faster.dao.query");
        exporter.setBeanPackageName("canuran.faster.dao.entity");
        exporter.setBeanInterfaces(new String[]{java.io.Serializable.class.getName()});
        ReflectionTestUtils.setField(exporter, "beanAddToString", true);
        ReflectionTestUtils.setField(exporter, "exportPrimaryKeys", true);
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
