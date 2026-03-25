package com.fremontfades.backend.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fremontfades.backend.Main;
import com.fremontfades.backend.model.Challenge;
import com.fremontfades.backend.model.Fight;
import com.fremontfades.backend.model.Fighter;
import com.fremontfades.backend.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FightHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final List<Fight> fights = new ArrayList<>();
    public static final Map<String, Challenge> challenges = new ConcurrentHashMap<>();

    public FightHandler() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("POST".equals(method) && "/api/fights".equals(path)) {
                handleCreateFight(exchange);
            } else if ("GET".equals(method) && "/api/fights".equals(path)) {
                handleGetFights(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/") && path.endsWith("/book")) {
                handleBookTicket(exchange);
            } else if ("POST".equals(method) && "/api/fights/challenges".equals(path)) {
                handleCreateChallenge(exchange);
            } else if ("GET".equals(method) && path.startsWith("/api/fights/challenges/")) {
                handleGetChallenges(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/challenges/") && path.endsWith("/accept")) {
                handleAcceptChallenge(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/challenges/") && path.endsWith("/reject")) {
                handleRejectChallenge(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/challenges/") && path.contains("/proposeLocation")) {
                handleProposeLocationAndTime(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/challenges/") && path.contains("/chat")) {
                handleChat(exchange);
            } else if ("POST".equals(method) && path.startsWith("/api/fights/challenges/") && path.contains("/agreeLocation")) {
                handleAgreeLocation(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void handleCreateChallenge(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
            String challengerUsername = body.get("challenger");
            String challengedUsername = body.get("challenged");

            if (!Main.users.containsKey(challengerUsername) || !Main.users.containsKey(challengedUsername)) {
                sendResponse(exchange, 400, "Invalid users");
                return;
            }

            String challengeId = UUID.randomUUID().toString();
            Challenge challenge = new Challenge(challengeId, challengerUsername, challengedUsername);

            boolean isBot = isBotUser(challengedUsername);
            if (isBot) {
                challenge.setStatus(Challenge.ChallengeStatus.ACCEPTED);
            }

            challenges.put(challengeId, challenge);
            sendResponse(exchange, 201, objectMapper.writeValueAsString(challenge));
        }
    }

    private boolean isBotUser(String username) {
        return java.util.Arrays.asList(
            "Akilesh Senthil", "Virinchi Kothapalli", "Arav Bhise",
            "Sachin Hedaoo", "Ashwin Marimuthu", "Sorodeep Deb",
            "Mr. Jan", "Abhay Sudhir", "Aryan Sudhir",
            "Ashish Swaminathan", "Avi Kumar"
        ).contains(username);
    }

    private void handleGetChallenges(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length >= 5) {
            String username = parts[4];
            List<Challenge> userChallenges = challenges.values().stream()
                    .filter(c -> c.getChallenger().equals(username) || c.getChallenged().equals(username))
                    .collect(Collectors.toList());
            sendResponse(exchange, 200, objectMapper.writeValueAsString(userChallenges));
        } else {
            sendResponse(exchange, 400, "Missing user parameter");
        }
    }

    private void handleAcceptChallenge(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String challengeId = path.split("/")[4];
        Challenge challenge = challenges.get(challengeId);

        if (challenge != null && challenge.getStatus() == Challenge.ChallengeStatus.PENDING) {
            challenge.setStatus(Challenge.ChallengeStatus.ACCEPTED);
            sendResponse(exchange, 200, "Challenge accepted");
        } else {
            sendResponse(exchange, 404, "Challenge not found or not pending");
        }
    }

    private void handleRejectChallenge(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String challengeId = path.split("/")[4];
        Challenge challenge = challenges.get(challengeId);

        if (challenge != null && challenge.getStatus() == Challenge.ChallengeStatus.PENDING) {
            challenge.setStatus(Challenge.ChallengeStatus.REJECTED);
            sendResponse(exchange, 200, "Challenge rejected");
        } else {
            sendResponse(exchange, 404, "Challenge not found or not pending");
        }
    }

    private void handleProposeLocationAndTime(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String challengeId = path.split("/")[4];
        Challenge challenge = challenges.get(challengeId);

        if (challenge != null && (challenge.getStatus() == Challenge.ChallengeStatus.ACCEPTED || challenge.getStatus() == Challenge.ChallengeStatus.LOCATION_PROPOSED)) {
            try (InputStream requestBody = exchange.getRequestBody()) {
                Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
                challenge.setProposedLocation(body.get("location"));
                challenge.setProposedTime(LocalDateTime.parse(body.get("time")));
                challenge.setLocationProposer(body.get("username"));
                challenge.setStatus(Challenge.ChallengeStatus.LOCATION_PROPOSED);

                if (isBotUser(challenge.getChallenged())) {
                    challenge.setStatus(Challenge.ChallengeStatus.SCHEDULED);

                    Fighter f1 = (Fighter) Main.users.get(challenge.getChallenger());
                    Fighter f2 = (Fighter) Main.users.get(challenge.getChallenged());

                    Fight newFight = new Fight(f1, f2, challenge.getProposedTime(), challenge.getProposedLocation(), 100);
                    fights.add(newFight);
                }

                sendResponse(exchange, 200, "Location and time proposed");
            }
        } else {
            sendResponse(exchange, 404, "Challenge not in right state");
        }
    }

    private void handleAgreeLocation(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String challengeId = path.split("/")[4];
        Challenge challenge = challenges.get(challengeId);

        if (challenge != null && challenge.getStatus() == Challenge.ChallengeStatus.LOCATION_PROPOSED) {
            try (InputStream requestBody = exchange.getRequestBody()) {
                Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
                String agreeingUser = body.get("username");

                if (!agreeingUser.equals(challenge.getLocationProposer())) {
                    challenge.setStatus(Challenge.ChallengeStatus.SCHEDULED);

                    Fighter f1 = (Fighter) Main.users.get(challenge.getChallenger());
                    Fighter f2 = (Fighter) Main.users.get(challenge.getChallenged());

                    Fight newFight = new Fight(f1, f2, challenge.getProposedTime(), challenge.getProposedLocation(), 100);
                    fights.add(newFight);

                    sendResponse(exchange, 200, "Location agreed and fight scheduled");
                } else {
                    sendResponse(exchange, 400, "You cannot agree to your own proposal");
                }
            }
        } else {
            sendResponse(exchange, 404, "Challenge not in right state to agree");
        }
    }

    private void handleChat(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String challengeId = path.split("/")[4];
        Challenge challenge = challenges.get(challengeId);

        if (challenge != null) {
            try (InputStream requestBody = exchange.getRequestBody()) {
                Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
                challenge.addChatMessage(body.get("sender"), body.get("message"));
                sendResponse(exchange, 200, "Message added");
            }
        } else {
            sendResponse(exchange, 404, "Challenge not found");
        }
    }

    private void handleCreateFight(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
            String fighter1Username = body.get("fighter1");
            String fighter2Username = body.get("fighter2");
            String location = body.get("location");
            int totalTickets = Integer.parseInt(body.get("totalTickets"));
            LocalDateTime fightDateTime = LocalDateTime.parse(body.get("fightDateTime"));

            User user1 = Main.users.get(fighter1Username);
            User user2 = Main.users.get(fighter2Username);

            if (user1 instanceof Fighter && user2 instanceof Fighter) {
                Fighter fighter1 = (Fighter) user1;
                Fighter fighter2 = (Fighter) user2;
                Fight fight = new Fight(fighter1, fighter2, fightDateTime, location, totalTickets);
                fights.add(fight);
                sendResponse(exchange, 201, "Fight created successfully");
            } else {
                sendResponse(exchange, 400, "Invalid fighters");
            }
        }
    }

    private void handleGetFights(HttpExchange exchange) throws IOException {
        String response = objectMapper.writeValueAsString(fights);
        sendResponse(exchange, 200, response);
    }

    private void handleBookTicket(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int fightIndex = Integer.parseInt(path.split("/")[3]);

        if (fightIndex >= 0 && fightIndex < fights.size()) {
            Fight fight = fights.get(fightIndex);
            if (fight.bookTickets(1)) {
                sendResponse(exchange, 200, "Ticket booked successfully");
            } else {
                sendResponse(exchange, 400, "No more tickets available");
            }
        } else {
            sendResponse(exchange, 404, "Fight not found");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
