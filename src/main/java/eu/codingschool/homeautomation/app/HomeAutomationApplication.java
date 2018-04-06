package eu.codingschool.homeautomation.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("eu.codingschool.homeautomation.model")
public class HomeAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeAutomationApplication.class, args);
	}
}
