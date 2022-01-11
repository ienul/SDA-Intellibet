package com.intellibet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan("com.intellibet")
public class IntellibetApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntellibetApplication.class, args);
	}

}
