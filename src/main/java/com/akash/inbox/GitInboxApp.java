package com.akash.inbox;

import com.akash.inbox.emailList.EmailListItem;
import com.akash.inbox.emailList.EmailListItemKey;
import com.akash.inbox.emailList.EmailListItemRepository;
import com.akash.inbox.folders.Folder;
import com.akash.inbox.folders.FolderRepository;
import com.datastax.oss.driver.api.core.uuid.Uuids;
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
import java.nio.file.Path;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class GitInboxApp {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	EmailListItemRepository emailListItemRepository;

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
		folderRepository.save(new Folder("pal-akash", "Inbox", "blue"));
		folderRepository.save(new Folder("pal-akash", "Sent", "green"));
		folderRepository.save(new Folder("pal-akash", "Important", "yellow"));

		for (int i=0; i<10; i++){
			EmailListItemKey key = new EmailListItemKey();
			key.setId("pal-akash");
			key.setLabel("Inbox");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("pal-akash"));
			item.setSubject("Subject " + i);
			item.setUnread(true);

			emailListItemRepository.save(item);
		}

	}

}
