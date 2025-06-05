
package com.example.account.controller;

import com.example.account.dto.LessonRequest;
import com.example.account.model.Lesson;
import com.example.account.model.User;
import com.example.account.repository.LessonRepository;
import com.example.account.repository.UserRepository;
import com.example.account.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonService lessonService;

    public LessonController(UserRepository userRepository, LessonRepository lessonRepository, LessonService lessonService) {
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@RequestBody LessonRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user");
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setUser(user);
        lessonRepository.save(lesson);

        return ResponseEntity.ok("Lesson created");
    }

    @GetMapping
    public List<Lesson> listLessons() {
        return lessonService.getAllLessons();
    }

    @GetMapping("/{id}")
    public Lesson getLesson(@PathVariable Long id) {
        return lessonService.getLesson(id);
    }

    @PutMapping("/{id}")
    public Lesson updateLesson(@PathVariable Long id,
                               @RequestBody Lesson lesson) {
        return lessonService.updateLesson(id, lesson, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id, null);
        return ResponseEntity.ok().build();
    }
}

