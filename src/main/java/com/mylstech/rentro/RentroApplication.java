package com.mylstech.rentro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RentroApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentroApplication.class, args);
	}

}
