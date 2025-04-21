package com.example.part35teammonew;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Part35teamMonewApplication {

  public static void main(String[] args) {
    SpringApplication.run(Part35teamMonewApplication.class, args);
  }
	
}
