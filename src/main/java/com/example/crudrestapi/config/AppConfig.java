package com.example.crudrestapi.config;

import com.example.crudrestapi.repository.base.BaseRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @author KAMSAN TUTORIAL
 */
@Configuration
@EnableWebMvc // Enable Spring Boot's default MVC configuration
@EnableJpaRepositories(basePackages = "com.example.crudrestapi", repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Use allowedOriginPatterns instead of allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
