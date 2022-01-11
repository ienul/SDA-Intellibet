package com.intellibet.controller;

import com.intellibet.dto.EventForm;
import com.intellibet.model.User;
import com.intellibet.service.EventService;
import com.intellibet.service.UserService;
import com.intellibet.validator.EventFormValidator;
//import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
@SpringBootApplication
public class AdminController {


  @Autowired
  private EventFormValidator eventFormValidator;

  @Autowired
  private UserService userService;

  @Autowired
  private EventService eventService;

  @GetMapping({"/newEvent"})
  public String getNewEventPage(Model model) {
    model.addAttribute("eventForm", new EventForm());
    return "newEvent";
  }

  @PostMapping({"/newEvent"})
  public String postNewEventPage(@ModelAttribute("eventForm") EventForm eventForm, BindingResult bindingResult) {

    //functia @ModelAttibute se numeste decorator. Orice decorator (@whatever("ceva")) aflat inaintea unei functii/element
    //in cazul asta, el el ajuta @PostMapping sa gaseasca eventForm de tip EventForm in dto-urile mele.
    eventFormValidator.validate(eventForm, bindingResult);
    if (bindingResult.hasErrors()) {
      return "newEvent";
    }

    eventService.save(eventForm);

    return "redirect:/home";
  }


  @GetMapping({"/users"})
  public String listAllUsers(Model model) {
    List<User> userList = userService.getAllUsers();
    System.out.println("===================================="+userList);
    model.addAttribute("userList", userList);
    return "userList";
  }
//aici urmeaza sa creez o cerere noua in AdminController catre userService sa apeleze o metoda noua, getUserById
  // pe care sa o afiseze in pagina http://localhost:8080/users/id in care "id" este variabila.

  @GetMapping({"/users/{id}"})
  public String listUsersById(Model model, @PathVariable Long id) {
    User userListById = userService.getUserById(id);
    System.out.println("=========FRATE A MERS,...Sau nu,... vedem" + userListById);
    model.addAttribute("userListById", userListById);
    return "userProfile";
  }


}