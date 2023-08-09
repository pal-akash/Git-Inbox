package com.akash.inbox.controller;

import com.akash.inbox.email.Email;
import com.akash.inbox.email.EmailRepository;
import com.akash.inbox.email.EmailService;
import com.akash.inbox.emailList.EmailListItem;
import com.akash.inbox.emailList.EmailListItemKey;
import com.akash.inbox.emailList.EmailListItemRepository;
import com.akash.inbox.folders.Folder;
import com.akash.inbox.folders.FolderRepository;
import com.akash.inbox.folders.FolderService;
import com.akash.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @Autowired
    private EmailService emailService;



    @GetMapping(value = "/emails/{id}")
    public String emailView(@RequestParam String folder, @PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        //Fetch folders
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);

        List<Folder> defaultFolders = folderService.fetchDefaultFolder(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("userName", principal.getAttribute("name"));

        Optional<Email> optionalEmail =  emailRepository.findById(id);
        if(optionalEmail.isEmpty()){
            return "inbox-page";
        }
        Email email =  optionalEmail.get();
        String toIds = String.join(",", email.getTo());

        //Check if user is allowed to see the email i.e. users other than sender and receivers
        if(!emailService.doesHaveAccess(email, userId)){
            return "redirect:/";
        }

        model.addAttribute("email", email);
        model.addAttribute("toIds", toIds);

        EmailListItemKey key = new EmailListItemKey();
        key.setId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getId());

        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
        if(optionalEmailListItem.isPresent()){
            EmailListItem emailListItem = optionalEmailListItem.get();
            if(emailListItem.getUnread()){
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCount(userId, folder);
            }
        }
        model.addAttribute("stats", folderService.mapCountToLabels(userId));

        return "email-page";
    }
}