package com.fremontfades.backend.model;

import java.time.LocalDateTime;

public class Fight {
    private Fighter fighter1;
    private Fighter fighter2;
    private LocalDateTime fightDateTime;
    private String location;
    private int totalTickets;
    private int ticketsSold;
    private Fighter winner;
    private boolean completed;

    public Fight(Fighter fighter1, Fighter fighter2, LocalDateTime fightDateTime, String location, int totalTickets) {
        this.fighter1 = fighter1;
        this.fighter2 = fighter2;
        this.fightDateTime = fightDateTime;
        this.location = location;
        this.totalTickets = totalTickets;
        this.ticketsSold = 0;
        this.completed = false;
        this.winner = null;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Fighter getWinner() {
        return winner;
    }

    public void setWinner(Fighter winner) {
        this.winner = winner;
        this.completed = true;

        if (winner != null) {
            Fighter loser;
            if (winner == fighter1) {
                loser = fighter2;
            } else {
                loser = fighter1;
            }

            int winnerElo = winner.getElo();
            int loserElo = loser.getElo();

            if (winnerElo == loserElo) {
                winner.setElo(winnerElo + 30);
                loser.setElo(loserElo - 30);
            } else {
                winner.updateElo(loserElo, true);
                loser.updateElo(winnerElo, false);
            }

            winner.recordWin();
            loser.recordLoss();
        }
    }
}
