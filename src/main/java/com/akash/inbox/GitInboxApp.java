package com.akash.inbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@SpringBootApplication
//@EnableConfigurationProperties(DataStaxAstraProperties.class)
@RestController
public class GitInboxApp {

	public static void main(String[] args) {
		SpringApplication.run(GitInboxApp.class, args);
	}

//	@RequestMapping("/user")
//	public Mono<String> user(@AuthenticationPrincipal Mono<OAuth2User> principal) {
//		return principal.map(val -> val.getAttribute("login"));
//	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties){
		Path bundle=astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

}
