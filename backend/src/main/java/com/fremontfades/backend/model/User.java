package com.fremontfades.backend.model;

public class User {
    private String username;
    private String password;
    private double credits;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.credits = 100.0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public void addCredits(double amount) {
        this.credits += amount;
    }

    public boolean redeemCredits(double amount) {
        if (this.credits >= amount) {
            this.credits -= amount;
            return true;
        }
        return false;
    }
}