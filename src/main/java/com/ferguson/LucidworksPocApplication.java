package com.ferguson;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class LucidworksPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(LucidworksPocApplication.class, args);
	}

}
