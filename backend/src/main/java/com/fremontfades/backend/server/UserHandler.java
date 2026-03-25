package com.fremontfades.backend.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fremontfades.backend.Main;
import com.fremontfades.backend.model.Fighter;
import com.fremontfades.backend.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserHandler implements HttpHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            if ("/api/users/register".equals(exchange.getRequestURI().getPath())) {
                handleRegister(exchange);
            } else if ("/api/users/login".equals(exchange.getRequestURI().getPath())) {
                handleLogin(exchange);
            }
        } else if ("GET".equals(exchange.getRequestMethod())) {
            if ("/api/users/fighters".equals(exchange.getRequestURI().getPath())) {
                handleGetFighters(exchange);
            }
        }
    }

    private void handleGetFighters(HttpExchange exchange) throws IOException {
        List<Fighter> fighters = Main.users.values().stream()
                .filter(user -> user instanceof Fighter)
                .map(user -> (Fighter) user)
                .collect(Collectors.toList());
        String response = objectMapper.writeValueAsString(fighters);
        sendResponse(exchange, 200, response);
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
            String username = body.get("username");
            String password = body.get("password");
            String role = body.get("role");

            if (username != null && password != null && !Main.users.containsKey(username)) {
                if ("fighter".equalsIgnoreCase(role)) {
                    int age = Integer.parseInt(body.get("age"));
                    double weight = Double.parseDouble(body.get("weight"));
                    double height = Double.parseDouble(body.get("height"));
                    Main.users.put(username, new com.fremontfades.backend.model.Fighter(username, password, age, weight, height));
                } else {
                    Main.users.put(username, new User(username, password));
                }
                sendResponse(exchange, 201, "User registered successfully");
            } else {
                sendResponse(exchange, 400, "Invalid username or password, or user already exists");
            }
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            Map<String, String> body = objectMapper.readValue(requestBody, Map.class);
            String username = body.get("username");
            String password = body.get("password");

            User user = Main.users.get(username);
            if (user != null && user.getPassword().equals(password)) {
                sendResponse(exchange, 200, "Login successful");
            } else {
                sendResponse(exchange, 401, "Invalid username or password");
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }}
}
