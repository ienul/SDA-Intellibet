package com.intellibet.service;


import com.intellibet.dto.EventForm;
import com.intellibet.mapper.EventMapper;
import com.intellibet.model.Bet;
import com.intellibet.model.BettingOption;
import com.intellibet.model.Event;
import com.intellibet.model.User;
import com.intellibet.repository.EventRepository;
import com.intellibet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public void save(EventForm eventFormDto) {
        Event eventEntity = eventMapper.map(eventFormDto); //aici am transformat obiectul dto intr-un entity. Mapper-ul schimba din ceva in ceva
        eventRepository.save(eventEntity);
    }

    public List<EventForm> retrieveAllEvents() { // functia care imi gaseste toate evenimentele sub forma de lista

        List<Event> allEvents = eventRepository.findAll(); //returneaza din DB o lista de entitati de tip "event"
        List<EventForm> result = new ArrayList<>();  // e creata o lista noua "result" de event dto-uri

        for (Event event : allEvents) {  // pentru fiecare event entity, il transformam in event dto (eventMapper.map(event)) si
            result.add(eventMapper.map(event)); //  apoi il adaug in lista de "result" declarata mai sus
        }

        return result;
    }

    public List<EventForm> retrieveFutureEvents() {

        List<Event> futureEvents = eventRepository.findEventsAfterDateTime(LocalDateTime.now());
        List<EventForm> result = new ArrayList<>();

        for (Event event : futureEvents) {
            result.add(eventMapper.map(event));
        }

        return result;
    }

    public void processPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> eventsBeforeDateTime = eventRepository.findEventsBeforeDateTime(now);
        if (eventsBeforeDateTime.size() == 0) {
            return;
        }
        generateRandomOutcomeFor(eventsBeforeDateTime);
        updateEvents(eventsBeforeDateTime);
        updateWinningBets(eventsBeforeDateTime);
    }

    private void updateWinningBets(List<Event> eventsBeforeDateTime) {
        for (Event element : eventsBeforeDateTime) {
            updateWinningBets(element);
        }
    }

    private void updateWinningBets(Event event) {

        Set<Bet> winningBets = event.getBets()
                .stream()
                .filter(bet -> bet.isWon().isPresent() && bet.isWon().get())
                .collect(Collectors.toSet());

        Set<User> rewardedUsers = new HashSet<>();
        for (Bet bet : winningBets) {
            User user = rewardUser(bet);
            rewardedUsers.add(user);
        }
        for (User user : rewardedUsers) {
            userRepository.save(user);
        }

    }

    private User rewardUser(Bet bet) {
        double howMuchUserWon = bet.getOdd() * bet.getValue();
        User user = bet.getUser();
        user.reward(howMuchUserWon);
        return user;
    }

    private void updateEvents(List<Event> eventsBeforeDateTime) {
        for (Event event : eventsBeforeDateTime) {
            eventRepository.save(event);
        }
    }

    private void generateRandomOutcomeFor(List<Event> eventsBeforeDateTime) {
        for (Event element : eventsBeforeDateTime) {
            element.setOutcome(BettingOption.getRandomOption());
        }
    }
}
