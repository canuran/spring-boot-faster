package ewing.faster.application.config;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import ewing.query.support.FriendlySQLLogger;
import ewing.query.support.SafeConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Querydsl 配置类。
 *
 * @author Ewing
 */
@Configuration
public class FasterDaoConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SQLQueryFactory queryFactory() {
        SQLTemplates templates = MySQLTemplates.builder().quote().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        SafeConnectionProvider safeConnectionProvider = new SafeConnectionProvider(dataSource, configuration);
        configuration.addListener(new FriendlySQLLogger());
        return new SQLQueryFactory(configuration, safeConnectionProvider);
    }

}