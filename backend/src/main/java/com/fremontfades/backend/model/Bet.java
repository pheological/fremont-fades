package com.fremontfades.backend.model;

public class Bet {
    private User user;
    private Fight fight;
    private Fighter chosenFighter;
    private double amount;

    public Bet(User user, Fight fight, Fighter chosenFighter, double amount) {
        this.user = user;
        this.fight = fight;
        this.chosenFighter = chosenFighter;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public Fight getFight() {
        return fight;
    }

    public Fighter getChosenFighter() {
        return chosenFighter;
    }

    public double getAmount() {
        return amount;
    }
}

