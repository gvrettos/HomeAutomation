package eu.codingschool.homeautomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Having the main class inside the root package requires only @SpringBootApplication to work
// Having @ComponentScan, @EnableJpaRepositories confuses @WebMvcTest testing
//@EntityScan("eu.codingschool.homeautomation.model")
//@ComponentScan(basePackages = "eu.codingschool.homeautomation")
//@EnableJpaRepositories("eu.codingschool.homeautomation.repositories")
public class HomeAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeAutomationApplication.class, args);
	}
}
