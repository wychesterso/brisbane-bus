package com.wychesterso.transit.brisbane_bus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrisbaneBusApplication {

	public static void main(String[] args) {
		System.out.println("MAIN METHOD STARTED");
		SpringApplication.run(BrisbaneBusApplication.class, args);
	}

}
