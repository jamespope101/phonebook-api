package com.jamespope101.phonebook;

import java.sql.Driver;
import java.util.Properties;
import javax.inject.Named;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by jpope on 07/02/2018.
 */
@EnableAutoConfiguration
@EnableTransactionManagement
@Configuration
@ComponentScan({"com.jamespope101.phonebook"})
@EnableAspectJAutoProxy
public class ApplicationConfig {

    private static final String[] HIBERNATE_PACKAGES = {
        "com.jamespope101.phonebook.domain",
    };

    @Bean
    DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl,
                          @Value("${jdbc.user}") String jdbcUsername,
                          @Value("${jdbc.pass}") String jdbcPassword,
                          @Value("${jdbc.driverClassName}") Class<? extends Driver> driverClass) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(jdbcUsername);
        dataSource.setPassword(jdbcPassword);
        dataSource.setDriverClassName(driverClass.getName());


        return dataSource;
    }

    @Bean
    @Named("hibernateProperties")
    Properties hibernateProperties(@Value("${hibernate.dialect}") final Class<? extends Dialect> dialect,
                                   @Value("${hibernate.show_sql}") final String showSql,
                                   @Value("${hibernate.hbm2ddl.auto:none}") String hbm2ddl) {

        final Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", showSql);
        properties.put("hibernate.hbm2ddl.auto", hbm2ddl);
        return properties;
    }

    @Bean
    LocalSessionFactoryBean localSessionFactoryBean(DataSource dataSource,
                                                    @Qualifier("hibernateProperties") Properties hibernateProperties) {

        LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
        localSessionFactoryBean.setDataSource(dataSource);
        localSessionFactoryBean.setHibernateProperties(hibernateProperties);
        localSessionFactoryBean.setAnnotatedPackages(HIBERNATE_PACKAGES);
        localSessionFactoryBean.setPackagesToScan(HIBERNATE_PACKAGES);
        return localSessionFactoryBean;
    }

    @Bean
    PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

}
