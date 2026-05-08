package com.library.bean;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String email;
    private int status; // 1正常 0禁用

    public User() {}
    public User(int userId, String username, String role, int status) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.status = status;
    }
    // getters and setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}