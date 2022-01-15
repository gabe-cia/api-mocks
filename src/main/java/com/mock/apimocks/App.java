package com.mock.apimocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "com.mock.apimocks")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
