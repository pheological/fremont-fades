package com.fremontfades.backend.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fremontfades.backend.model.Bet;
import com.fremontfades.backend.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BetHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final List<Bet> bets = new ArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()) && "/api/bets".equals(exchange.getRequestURI().getPath())) {
            handlePlaceBet(exchange);
        }
    }

    private void handlePlaceBet(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
            String username = body.get("username");
            int fightIndex = Integer.parseInt(body.get("fightIndex"));
            String fighterUsername = body.get("fighter");
            double amount = Double.parseDouble(body.get("amount"));

            User user = com.fremontfades.backend.Main.users.get(username);
            if (user != null && user.getCredits() >= amount) {
                if (fightIndex >= 0 && fightIndex < FightHandler.fights.size()) {
                    User fighterUser = com.fremontfades.backend.Main.users.get(fighterUsername);
                    if (fighterUser instanceof com.fremontfades.backend.model.Fighter) {
                        com.fremontfades.backend.model.Fighter chosenFighter = (com.fremontfades.backend.model.Fighter) fighterUser;
                        user.redeemCredits(amount);
                        Bet bet = new Bet(user, FightHandler.fights.get(fightIndex), chosenFighter, amount);
                        bets.add(bet);
                        sendResponse(exchange, 201, "Bet placed successfully");
                    } else {
                        sendResponse(exchange, 400, "Invalid fighter selected");
                    }
                } else {
                    sendResponse(exchange, 404, "Fight not found");
                }
            } else {
                sendResponse(exchange, 400, "Invalid user or insufficient credits");
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

