package com.example02.demo02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@ServletComponentScan
public class Demo02Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Demo02Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(this.getClass());
	}

}
