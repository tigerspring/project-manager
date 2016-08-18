package com.vcg.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.alibaba.druid.pool.DruidDataSource;


@Configuration
@EnableTransactionManagement
public class JdbcConfig implements  TransactionManagementConfigurer {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
		return new DruidDataSource();
    }
	
	
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		
		return new DataSourceTransactionManager(dataSource());
	}
	
	
 	
}
