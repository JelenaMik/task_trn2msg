package com.example.task.controller;

import com.example.task.domain.Notification;
import com.example.task.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService service;

    @GetMapping("/notification")
    public ResponseEntity<Notification> createRoot(){
        return ResponseEntity.ok(service.printNotification());
    }
}
