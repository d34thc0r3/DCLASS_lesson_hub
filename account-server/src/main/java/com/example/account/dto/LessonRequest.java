// LessonRequest.java
package com.example.account.dto;

public class LessonRequest {
    private String title;
    private String description;
    private String username; // <- 이 필드를 추가

    // getter, setter 추가
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
