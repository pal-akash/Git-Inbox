package com.akash.inbox.controller;

import com.akash.inbox.email.Email;
import com.akash.inbox.email.EmailRepository;
import com.akash.inbox.email.EmailService;
import com.akash.inbox.folders.Folder;
import com.akash.inbox.folders.FolderRepository;
import com.akash.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping(value = "/compose")
    public String getComposePage(@RequestParam(required = false) String to, @RequestParam(required = false) UUID id, @AuthenticationPrincipal OAuth2User principal, Model model){

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

        model.addAttribute("stats", folderService.mapCountToLabels(userId));
        List<String> uniqueToIds = splitIds(to);
        model.addAttribute("to", String.join(", ", uniqueToIds));

        Optional<Email> optionalEmail =  emailRepository.findById(id);
        if(optionalEmail.isPresent()){
            Email email =  optionalEmail.get();
            if(emailService.doesHaveAccess(email, userId)){
                model.addAttribute("subject", emailService.getReplySubject(email.getSubject()));
                model.addAttribute("body", emailService.getReplyBody(email));
            }
        }

        return "compose-page";
    }

    private static List<String> splitIds(String to) {
        if(!StringUtils.hasText(to)){
            return new ArrayList<String>();
        }
        String[] splitIds = to.split(",");
        List<String> uniqueToIds = Arrays.asList(splitIds)
                .stream()
                .map(id->StringUtils.trimWhitespace(id))
                .filter(id->StringUtils.hasText(id))
                .distinct()
                .collect(Collectors.toList());
        return uniqueToIds;
    }


    @PostMapping("/sendEmail")
    public ModelAndView sendEmail(@RequestBody MultiValueMap<String, String> fromData, @AuthenticationPrincipal OAuth2User principal){

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return new ModelAndView("redirect:/");
        }
        String from = principal.getAttribute("login");
        List<String> toIds = splitIds(fromData.getFirst("to"));
        String subject = fromData.getFirst("subject");
        String body = fromData.getFirst("body");

        emailService.sendEmail(from, toIds, subject, body);
        return new ModelAndView("redirect:/");

    }
}
