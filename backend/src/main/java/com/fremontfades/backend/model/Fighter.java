package com.fremontfades.backend.model;

public class Fighter extends User {
    private int age;
    private double weight;
    private double height;
    private int wins;
    private int losses;
    private int elo;

    public Fighter(String username, String password, int age, double weight, double height) {
        super(username, password);
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.wins = 0;
        this.losses = 0;
        this.elo = 1000;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public void updateElo(int opponentElo, boolean won) {
        if (this.elo == opponentElo) {
            this.elo += won ? 30 : -30;
        } else {
            int kFactor = 32;
            double expectedScore = 1.0 / (1.0 + Math.pow(10, (opponentElo - this.elo) / 400.0));
            double actualScore = won ? 1.0 : 0.0;
            this.elo = (int) (this.elo + kFactor * (actualScore - expectedScore));
        }
    }

    public void recordWin() {
        this.wins++;
    }

    public void recordLoss() {
        this.losses++;
    }
}

