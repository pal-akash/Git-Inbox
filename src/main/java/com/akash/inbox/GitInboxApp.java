package com.akash.inbox;

import com.akash.inbox.email.EmailService;
import com.akash.inbox.folders.Folder;
import com.akash.inbox.folders.FolderRepository;
import com.akash.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class GitInboxApp {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	UnreadEmailStatsRepository unreadEmailStatsRepository;

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

	@PostConstruct
	public void init(){
		folderRepository.save(new Folder("pal-akash", "Family", "blue"));
		folderRepository.save(new Folder("pal-akash", "Home", "green"));
		folderRepository.save(new Folder("pal-akash", "Work", "yellow"));

		for (int i=0; i<10; i++){
			emailService.sendEmail("pal-akash", Arrays.asList("pal-akash", "abc"), "Hello " + i, "Howdy!");
		}

	}

}
