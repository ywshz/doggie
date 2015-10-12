package org.yws.doggieweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by ywszjut on 15/7/24.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.yws.doggieweb.repositories")
@EntityScan(basePackages = "org.yws.doggieweb.models")
public class WebUIApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(WebUIApplication.class);
	}

	public static void main(String[] args) {

		SpringApplication.run(WebUIApplication.class, args);

	}
}
