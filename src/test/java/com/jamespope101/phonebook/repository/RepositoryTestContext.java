package com.jamespope101.phonebook.repository;

import java.io.StringReader;
import javax.sql.DataSource;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by jpope on 07/02/2018.
 */
@EnableTransactionManagement
@ComponentScan(basePackages = "com.jamespope101.phonebook")
public class RepositoryTestContext {

    private static final String[] ENTITY_PACKAGES = {
        "com.jamespope101.phonebook.domain",
    };

    @Bean
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    LocalSessionFactoryBean sessionFactoryBean(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(ENTITY_PACKAGES);
        sessionFactoryBean.getHibernateProperties().setProperty("hibernate.hbm2ddl.auto", "create-drop");
        sessionFactoryBean.getHibernateProperties().setProperty("hibernate.show_sql", "false");
        sessionFactoryBean.getHibernateProperties().setProperty("hibernate.format_sql", "true");
        sessionFactoryBean.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        return sessionFactoryBean;
    }

    @Bean
    PlatformTransactionManager txManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    public static FlatXmlDataSet createDataSet(String... dataSetRows) throws DataSetException {
        final StringBuilder datasetBuilder = new StringBuilder("<dataset>");

        newArrayList(dataSetRows).forEach(datasetBuilder::append);

        return new FlatXmlDataSetBuilder()
            .setColumnSensing(true)
            .build(new StringReader(datasetBuilder.append("</dataset>").toString()));
    }
}
