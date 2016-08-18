package com.vcg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.vcg.code")
@MapperScan("com.vcg.code.dao")
@EnableSwagger2
public class ProjectManagerMain {

	public static void main(String[] args) throws IOException {
		//pid 方便 用kill关闭
		String name = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		FileWriter writer=new FileWriter(new File("main.pid"));
		writer.write(name);
		writer.close();
		SpringApplication.run(ProjectManagerMain.class, args);
	}
}
