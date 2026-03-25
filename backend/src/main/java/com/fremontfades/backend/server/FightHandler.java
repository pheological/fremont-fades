package com.fremontfades.backend.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fremontfades.backend.Main;
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
import java.util.concurrent.ConcurrentHashMap;

public class FightHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final List<Fight> fights = new ArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equals(method) && "/api/fights".equals(path)) {
            handleCreateFight(exchange);
        } else if ("GET".equals(method) && "/api/fights".equals(path)) {
            handleGetFights(exchange);
        } else if ("POST".equals(method) && path.startsWith("/api/fights/") && path.endsWith("/book")) {
            handleBookTicket(exchange);
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

