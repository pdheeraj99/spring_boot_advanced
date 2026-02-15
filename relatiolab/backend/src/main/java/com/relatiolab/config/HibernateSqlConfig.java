package com.relatiolab.config;

import com.relatiolab.debug.HibernateSqlInspector;
import com.relatiolab.debug.SqlTraceStore;
import java.util.Map;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateSqlConfig {

    @Bean
    HibernatePropertiesCustomizer hibernatePropertiesCustomizer(SqlTraceStore sqlTraceStore) {
        return (Map<String, Object> properties) -> properties.put(
                AvailableSettings.STATEMENT_INSPECTOR,
                new HibernateSqlInspector(sqlTraceStore)
        );
    }
}