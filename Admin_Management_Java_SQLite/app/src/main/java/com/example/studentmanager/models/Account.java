package com.example.studentmanager.models;

public class Account {
    private final long id;
    private final String username;
    private final String password;
    private final String role;
    public Account(long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
