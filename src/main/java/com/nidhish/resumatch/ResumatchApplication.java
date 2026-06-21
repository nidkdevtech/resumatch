package com.nidhish.resumatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.nidhish.resumatch.service.EmbeddingService;

@SpringBootApplication
public class ResumatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumatchApplication.class, args);
	}

}
