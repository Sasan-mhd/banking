package com.sasan.banking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingApplication {

	@Bean
	public CommandLineRunner runUI(Bank bank) {
		return args -> {
			BankingSystemUI ui = new BankingSystemUI(bank);
			ui.run();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
