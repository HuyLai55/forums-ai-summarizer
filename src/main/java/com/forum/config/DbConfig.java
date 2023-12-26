package com.forum.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.forum"})
@ComponentScan(basePackages = {"com.forum"})
public class DbConfig {

}
