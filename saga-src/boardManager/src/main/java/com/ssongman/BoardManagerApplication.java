package com.ssongman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BoardManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardManagerApplication.class, args);
	}

}
