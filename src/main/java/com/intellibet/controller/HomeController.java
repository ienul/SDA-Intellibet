package com.intellibet.controller;

import com.intellibet.dto.*;
import com.intellibet.service.BetService;
import com.intellibet.service.EventService;
import com.intellibet.service.UserService;
import com.intellibet.validator.BetFormValidator;
import com.intellibet.validator.DepositFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@SpringBootApplication
public class HomeController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepositFormValidator depositFormValidator;
    @Autowired
    private BetFormValidator betFormValidator;
    @Autowired
    private BetService betService;

    @GetMapping({"/login", "/home", "/"})
    public String getHomePage(Model model, Authentication authentication) {
        model.addAttribute("userForm", new UserForm());

        List<EventForm> eventFormList = eventService.retrieveFutureEvents();
        model.addAttribute("eventFormList",eventFormList);

        BetForm betForm = betService.getBetFormFor(authentication);
        model.addAttribute("betForm", betForm);
        return "home";
    }

    @GetMapping({"/myAccount"})
    public String getMyAccountPage(Model model, Authentication authentication) {
        DepositForm depositForm = userService.getDepositFormBy(authentication.getName());
        model.addAttribute("depositForm", depositForm);


        return "myAccount";
    }

    @PostMapping({"/deposit"})
    public String postDepositRequest(@ModelAttribute("depositForm") DepositForm depositForm,
        BindingResult bindingResult, Authentication authentication) {
        depositFormValidator.validate(depositForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "myAccount";
        }

        String authenticatedUserEmail = authentication.getName();

        //aici vreau sa depun suma depositForm.getAmount() in contul cu email pun in variabila
        // authenticatedUserEmail
        userService.deposit(depositForm, authenticatedUserEmail);

        return "redirect:/myAccount";

    }

    @PostMapping({"/home"})
    public String postBetRequest(@ModelAttribute("betForm") BetForm betForm,
        BindingResult bindingResult, Model model, Authentication authentication) {
        List<EventForm> eventFormList = eventService.retrieveFutureEvents();
        model.addAttribute("eventFormList",eventFormList);

        betFormValidator.fullValidation(betForm, bindingResult, authentication.getName());
        if(bindingResult.hasErrors()){
            return "home";
        }
        //aici sigur avem un betForm valid
        betService.placeBet(betForm, authentication.getName());

        return "redirect:/myBets";

    }

    @GetMapping({"/myBets"})
    public String getMyBetsPage(Model model, Authentication authentication) {
        String userEmail = authentication.getName();
        List<PlacedBetForm> decidedBets = betService.getDecidedBetsFor(userEmail);
        model.addAttribute("decidedBets",decidedBets );

        List<PlacedBetForm> pendingBets = betService.getPendingBetsFor(userEmail);
        model.addAttribute("pendingBets",pendingBets );

        return "myBets";
    }

}
