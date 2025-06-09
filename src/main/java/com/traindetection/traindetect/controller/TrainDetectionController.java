package com.traindetection.traindetect.controller;

import com.traindetection.traindetect.model.TrainDetection;
import com.traindetection.traindetect.service.TrainDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trains")
public class TrainDetectionController {


    @Autowired
    private TrainDetectionService trainDetectionService;

    @GetMapping("/detections")
    public ResponseEntity<List<TrainDetection>> getRecentDetections() {
        return ResponseEntity.ok(trainDetectionService.getRecentDetections());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "running", trainDetectionService.isRunning(),
                "recentDetectionCount", trainDetectionService.getRecentDetections().size()
        ));
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startDetection() {
        trainDetectionService.startDetection();
        return ResponseEntity.ok(Map.of("message", "Train detection started"));
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stopDetection() {
        trainDetectionService.stopDetection();
        return ResponseEntity.ok(Map.of("message", "Train detection stopped"));
    }
}