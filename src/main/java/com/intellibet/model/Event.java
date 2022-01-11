package com.intellibet.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String playerA;
    private String playerB;
    private EventCategory category;
    private Float oddA;
    private Float oddB;
    private Float oddX;
    private LocalDateTime dateTime;
    private BettingOption outcome;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Bet> bets;

}
