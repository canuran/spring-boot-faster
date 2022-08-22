package canuran.query;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import canuran.query.support.FriendlySQLLogger;
import canuran.query.support.SafeSQLListener;
import canuran.query.support.SpringConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Querydsl 配置类。
 *
 * @author canuran
 */
@Configuration
public class QueryDaoConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public BaseQueryFactory queryFactory() {
        SQLTemplates templates = H2Templates.builder().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        configuration.addListener(new FriendlySQLLogger());
        configuration.addListener(new SafeSQLListener());
        return new BaseQueryFactory(configuration, new SpringConnectionProvider(dataSource, configuration));
    }

}