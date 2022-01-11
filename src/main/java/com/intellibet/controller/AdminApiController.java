package com.intellibet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminApiController {


    @PostMapping(value = {"/deleteEvent/{eventId}"})
    public ResponseEntity deleteEventPage(@PathVariable Long eventId){
        System.out.println("===========================" + eventId);
        return new ResponseEntity<>(eventId, HttpStatus.OK);
    }

}
