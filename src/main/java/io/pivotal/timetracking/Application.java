package io.pivotal.timetracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The Spring configuration and entry point for the application.
 * 
 * @author Brian Jimerson
 *
 */

@SpringBootApplication
public class Application {

	/**
	 * The main entry point for the Spring Boot application.
	 * 
	 * @param args Any command line arguments passed.
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
