package com.boeing.cloudview.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@PropertySource("classpath:cloudviewconfig.properties")
public class CloudViewSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudViewSearchApplication.class, args);
	}

}
