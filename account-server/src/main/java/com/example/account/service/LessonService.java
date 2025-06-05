
package com.example.account.service;

import com.example.account.model.Lesson;
import com.example.account.model.User;
import com.example.account.repository.LessonRepository;
import com.example.account.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    public Lesson createLesson(String username, Lesson lesson) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        lesson.setUser(user);
        lesson.setCreatedAt(LocalDateTime.now());
        return lessonRepository.save(lesson);
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson getLesson(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));
    }

    public Lesson updateLesson(Long id, Lesson newLesson, String username) {
        Lesson lesson = getLesson(id);
        if (!lesson.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not the owner");
        }
        lesson.setTitle(newLesson.getTitle());
        lesson.setDescription(newLesson.getDescription());
        lesson.setCategory(newLesson.getCategory());
        return lessonRepository.save(lesson);
    }

    public void deleteLesson(Long id, String username) {
        Lesson lesson = getLesson(id);
        if (!lesson.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not the owner");
        }
        lessonRepository.delete(lesson);
    }
}

