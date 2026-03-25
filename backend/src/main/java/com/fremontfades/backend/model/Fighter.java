package com.fremontfades.backend.model;

public class Fighter extends User {
    private int age;
    private double weight;
    private double height;
    private int wins;
    private int losses;

    public Fighter(String username, String password, int age, double weight, double height) {
        super(username, password);
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.wins = 0;
        this.losses = 0;
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

    public void recordWin() {
        this.wins++;
    }

    public void recordLoss() {
        this.losses++;
    }
}

