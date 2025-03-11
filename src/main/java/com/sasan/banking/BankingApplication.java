package com.sasan.banking;

import com.sasan.banking.application.Bank;
import com.sasan.banking.ui.console.ConsoleUI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankingApplication {

	@Bean
	@ConditionalOnProperty(name = "ui.enabled", havingValue = "true", matchIfMissing = true)
	public CommandLineRunner runUI(Bank bank) {
		return args -> {
			ConsoleUI ui = new ConsoleUI(bank);
			ui.run();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}

}
