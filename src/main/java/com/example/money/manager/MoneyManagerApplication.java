package com.example.money.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class MoneyManagerApplication {


	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApplication.class, args);
	}


}



/*netstat -ano | findstr :8080
taskkill /PID 12345 /F
 */