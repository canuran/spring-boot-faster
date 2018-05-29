package ewing.application.config;

import com.querydsl.core.support.QueryBase;
import com.querydsl.sql.*;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Provider;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Querydsl 配置类。
 *
 * @author Ewing
 */
@Configuration
public class QuerydslConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            SQLQueryFactory.class.getPackage().getName() + ".SQLParameters");

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
        configuration.addListener(new SQLBaseListener() {
            @Override
            public void preExecute(SQLListenerContext context) {
                LOGGER.debug(MDC.get(QueryBase.MDC_PARAMETERS));
            }
        });
        Provider<Connection> provider = new SpringConnectionProvider(dataSource);
        return new SQLQueryFactory(configuration, provider);
    }

}