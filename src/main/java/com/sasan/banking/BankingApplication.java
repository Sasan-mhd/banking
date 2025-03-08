package com.sasan.banking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class BankingApplication {

	@Bean
	@ConditionalOnProperty(name = "ui.enabled", havingValue = "true", matchIfMissing = true)	public CommandLineRunner runUI(Bank bank) {
		return args -> {
			BankingSystemUI ui = new BankingSystemUI(bank);
			ui.run();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
