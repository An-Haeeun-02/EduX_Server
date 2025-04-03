package com.Capstone.EduX;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.Capstone.EduX")
@EnableJpaRepositories(basePackages = "com.Capstone.EduX")
public class AppConfig { }
