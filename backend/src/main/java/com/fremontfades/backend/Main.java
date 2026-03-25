package com.fremontfades.backend;

import com.fremontfades.backend.model.User;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.fremontfades.backend.model.Fighter;

public class Main {
    public static final Map<String, User> users = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        users.put("Akilesh Senthil", new Fighter("Akilesh Senthil", "pass", 16, 200, 185));
        users.put("Virinchi Kothapalli", new Fighter("Virinchi Kothapalli", "pass", 16, 125, 174));
        users.put("Arav Bhise", new Fighter("Arav Bhise", "pass", 17, 135, 182));
        users.put("Sachin Hedaoo", new Fighter("Sachin Hedaoo", "pass", 17, 200, 190));
        users.put("Ashwin Marimuthu", new Fighter("Ashwin Marimuthu", "pass", 17, 150, 177));
        users.put("Sorodeep Deb", new Fighter("Sorodeep Deb", "pass", 18, 300, 160));
        users.put("Mr. Jan", new Fighter("Mr. Jan", "pass", 40, 165, 182));
        users.put("Abhay Sudhir", new Fighter("Abhay Sudhir", "pass", 17, 140, 174));
        users.put("Aryan Sudhir", new Fighter("Aryan Sudhir", "pass", 17, 135, 173));
        users.put("Ashish Swaminathan", new Fighter("Ashish Swaminathan", "pass", 16, 250, 170));
        users.put("Avi Kumar", new Fighter("Avi Kumar", "pass", 17, 145, 170));

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/users", new com.fremontfades.backend.server.UserHandler());
        server.createContext("/api/fights", new com.fremontfades.backend.server.FightHandler());
        server.createContext("/api/bets", new com.fremontfades.backend.server.BetHandler());
        server.createContext("/", new com.fremontfades.backend.server.StaticFileHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}
