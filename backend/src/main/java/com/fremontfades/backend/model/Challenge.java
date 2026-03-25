package com.fremontfades.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Challenge {
    private String id;
    private String challenger;
    private String challenged;
    private ChallengeStatus status;
    private LocalDateTime proposedTime;
    private String proposedLocation;
    private String locationProposer;
    private List<ChatMessage> chatHistory;

    public enum ChallengeStatus {
        PENDING, ACCEPTED, REJECTED, LOCATION_PROPOSED, SCHEDULED
    }

    public static class ChatMessage {
        public String sender;
        public String message;
        public LocalDateTime timestamp;

        public ChatMessage() {
        }

        public ChatMessage(String sender, String message) {
            this.sender = sender;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
    }

    public Challenge() {
    }

    public Challenge(String id, String challenger, String challenged) {
        this.id = id;
        this.challenger = challenger;
        this.challenged = challenged;
        this.status = ChallengeStatus.PENDING;
        this.chatHistory = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getChallenger() {
        return challenger;
    }
    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public String getChallenged() {
        return challenged;
    }
    public void setChallenged(String challenged) {
        this.challenged = challenged;
    }

    public ChallengeStatus getStatus() {
        return status;
    }
    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public LocalDateTime getProposedTime() {
        return proposedTime;
    }
    public void setProposedTime(LocalDateTime proposedTime) {
        this.proposedTime = proposedTime;
    }

    public String getProposedLocation() {
        return proposedLocation;
    }
    public void setProposedLocation(String proposedLocation) {
        this.proposedLocation = proposedLocation;
    }

    public String getLocationProposer() {
        return locationProposer;
    }
    public void setLocationProposer(String locationProposer) {
        this.locationProposer = locationProposer;
    }

    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }
    public void addChatMessage(String sender, String message) {
        this.chatHistory.add(new ChatMessage(sender, message));
    }
}

