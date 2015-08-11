package io.pivotal.timetracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


/**
 * The Spring configuration and entry point for
 * the application.
 * 
 * @author Brian Jimerson
 *
 */

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
public class Application {

	/**
	 * The main entry point for the Spring Boot application.
	 * @param args Any command line arguments passed.
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	

}
