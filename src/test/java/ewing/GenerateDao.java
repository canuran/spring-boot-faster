package ewing;

import com.querydsl.maven.AbstractMetaDataExportMojo;
import com.querydsl.maven.MetadataExportMojo;
import com.querydsl.sql.codegen.support.NumericMapping;
import ewing.application.config.SBFBasisDao;
import ewing.application.query.DaoGenerator;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * 自动生成Dao接口与实现，比Maven配置更灵活。
 *
 * @author Ewing
 * @since 2018年05月21日
 */
public class GenerateDao {

    /***
     * 生成Bean和QBean。
     */
    @Test
    public void generateMeta() throws Exception {
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
        exporter.setTargetFolder("src/main/java");
        exporter.setPackageName("ewing.dao1.query");
        exporter.setBeanPackageName("ewing.dao1.entity");
        exporter.setBeanInterfaces(new String[]{java.io.Serializable.class.getName()});
        Field beanAddToString = AbstractMetaDataExportMojo.class.getDeclaredField("beanAddToString");
        beanAddToString.setAccessible(true);
        beanAddToString.set(exporter, true);
        exporter.setCustomTypes(new String[]{com.querydsl.sql.types.UtilDateType.class.getName()});
        exporter.setNumericMappings(getNumericMappings());
        exporter.setTableNamePattern("%");
        exporter.execute();
    }

    /***
     * 生成Dao层代码。
     */
    @Test
    public void generateDao() throws Exception {
        new DaoGenerator()
                .daoSuperClass(SBFBasisDao.class)
                .javaCodePath("src/main/java")
                .daoPackage("ewing.dao1")
                .queryBeanPackage("ewing.dao1.query")
                .generate();
    }

    /**
     * 5位以下的整数统一使用Integer。
     */
    private static NumericMapping[] getNumericMappings() {
        NumericMapping[] numericMappings = new NumericMapping[5];
        for (int i = 0; i < numericMappings.length; i++) {
            numericMappings[i] = new NumericMapping();
            numericMappings[i].setDecimal(0);
            numericMappings[i].setTotal(i);
            numericMappings[i].setJavaType(Integer.class.getName());
        }
        return numericMappings;
    }

}
