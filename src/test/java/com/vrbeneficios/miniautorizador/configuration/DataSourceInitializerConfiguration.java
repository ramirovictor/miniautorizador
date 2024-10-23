package com.vrbeneficios.miniautorizador.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@TestConfiguration
public class DataSourceInitializerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializerConfiguration.class);

    @Bean(name = "DataSourceInitializer")
    public DataSourceInitializer DataSourceInitializer(DataSource datasource) {
        logger.info("Inicializando DataSourceInitializer");
        return dataSourceInitializer(datasource, "/scripts/database/miniautorizador/schema.sql", "/scripts/database/miniautorizador/data.sql");
    }

    private DataSourceInitializer dataSourceInitializer(DataSource dataSource, String... configFilesPath) {

        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        for (String path : configFilesPath) {
            resourceDatabasePopulator.addScript(new ClassPathResource(path));
        }

        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        logger.info("Scripts SQL carregados");
        return dataSourceInitializer;
    }

}