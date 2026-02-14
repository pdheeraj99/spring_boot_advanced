package com.example.lifecyclelab.config;

import com.example.lifecyclelab.beans.InitMethodBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public InitMethodBean initMethodBean() {
        return new InitMethodBean();
    }
}
