package io.javabrains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class GitInboxApp {

	public static void main(String[] args) {
		SpringApplication.run(GitInboxApp.class, args);
	}

//	@RequestMapping("/user")
//	public Mono<String> user(@AuthenticationPrincipal Mono<OAuth2User> principal) {
//		return principal.map(val -> val.getAttribute("login"));
//	}
	

}
