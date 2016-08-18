package com.vcg.config;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;



@Configuration
//@ServletComponentScan
public class WebConfig {
		
	/* @Bean
     public FilterRegistrationBean filterRegistrationBean(){  
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();  
        filterRegistrationBean.setFilter(new TestFilter());  
        filterRegistrationBean.setEnabled(true);  
       // filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.addUrlPatterns("/*");  
        return filterRegistrationBean;  
    }     
	
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new TestServlet(), "/test");
		servletRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
		return servletRegistrationBean;
	}

	*/
	
}
