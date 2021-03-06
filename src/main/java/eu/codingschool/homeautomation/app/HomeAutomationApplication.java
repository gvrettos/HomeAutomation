package eu.codingschool.homeautomation.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("eu.codingschool.homeautomation.model")
@ComponentScan(basePackages = "eu.codingschool.homeautomation")
@EnableJpaRepositories("eu.codingschool.homeautomation.repositories")
public class HomeAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeAutomationApplication.class, args);
	}
}
