package com.fremontfades.backend;

import com.fremontfades.backend.model.User;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static final Map<String, User> users = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/users/", new com.fremontfades.backend.server.UserHandler());
        server.createContext("/api/fights/", new com.fremontfades.backend.server.FightHandler());
        server.createContext("/api/bets/", new com.fremontfades.backend.server.BetHandler());
        server.createContext("/", new com.fremontfades.backend.server.StaticFileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}
