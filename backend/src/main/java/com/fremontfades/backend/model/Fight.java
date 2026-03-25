package com.fremontfades.backend.model;

import java.time.LocalDateTime;

public class Fight {
    private Fighter fighter1;
    private Fighter fighter2;
    private LocalDateTime fightDateTime;
    private String location;
    private int totalTickets;
    private int ticketsSold;

    public Fight(Fighter fighter1, Fighter fighter2, LocalDateTime fightDateTime, String location, int totalTickets) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.fightDateTime = fightDateTime;
        this.location = location;
        this.totalTickets = totalTickets;
        this.ticketsSold = 0;
    }

    public Fighter getFighter1() {
        return fighter1;
    }

    public Fighter getFighter2() {
        return fighter2;
    }

    public LocalDateTime getFightDateTime() {
        return fightDateTime;
    }

    public String getLocation() {
        return location;
    }

    public int getAvailableTickets() {
        return totalTickets - ticketsSold;
    }

    public boolean bookTickets(int quantity) {
        if (getAvailableTickets() >= quantity) {
            ticketsSold += quantity;
            return true;
        }
        return false;
    }
}

