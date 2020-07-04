package io.windflow.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class WindflowServer {

	public static void main(String[] args) {
		SpringApplication.run(WindflowServer.class, args);
	}

}
