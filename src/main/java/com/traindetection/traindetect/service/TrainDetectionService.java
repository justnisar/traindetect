package com.traindetection.traindetect.service;

import com.traindetection.traindetect.config.AudioConfig;
import com.traindetection.traindetect.model.TrainDetection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
public class TrainDetectionService {
    private static final Logger logger = LoggerFactory.getLogger(TrainDetectionService.class);

    @Autowired
    private AudioCaptureService audioCaptureService;

    @Autowired
    private SignalProcessingService signalProcessingService;

    @Autowired
    private TrainClassificationService trainClassificationService;

    @Autowired
    private AudioConfig audioConfig;

    private volatile boolean isRunning = false;
    private List<TrainDetection> recentDetections = new CopyOnWriteArrayList<>();


/*    @PostConstruct
    public void init() {
        startDetection();
    }

    @PreDestroy
    public void cleanup() {
        stopDetection();
    }*/



    public void startDetection() {
        if (isRunning) {
            logger.warn("Train detection is already running");
            return;
        }

        isRunning = true;
        audioCaptureService.startCapture();
        processAudioAsync();
        logger.info("Started train detection service");
    }

    public void stopDetection() {
        isRunning = false;
        audioCaptureService.stopCapture();
        logger.info("Stopped train detection service");
    }

    @Async
    public void processAudioAsync() {
        while (isRunning) {
            try {
                if (audioCaptureService.hasAudioData()) {
                    byte[] audioData = audioCaptureService.getNextAudioData();
                    processAudioData(audioData);
                } else {
                    Thread.sleep(10); // Short sleep to prevent busy waiting
                }
            } catch (InterruptedException e) {
                logger.info("Audio processing interrupted");
                break;
            } catch (Exception e) {
                logger.error("Error in audio processing: {}", e.getMessage());
            }
        }
    }

    private void processAudioData(byte[] audioData) {
        try {
            // Convert audio bytes to samples
            double[] samples = signalProcessingService.bytesToDoubles(audioData);

            // Apply windowing
            double[] windowedSamples = signalProcessingService.applyHammingWindow(samples);

            // Calculate RMS amplitude
            double amplitude = signalProcessingService.calculateRMS(windowedSamples);

            // Compute FFT spectrum
            double[] spectrum = signalProcessingService.computeFFT(windowedSamples);

            // Extract frequency bands
            double[] frequencyBands = signalProcessingService.extractFrequencyBands(
                    spectrum, (int) audioConfig.getSampleRate());

            // Classify potential train
            TrainDetection detection = trainClassificationService.classifyTrain(
                    frequencyBands, amplitude);

            if (detection != null) {
                detection.setFrequencyProfile(frequencyBands);
                recentDetections.add(detection);

                // Keep only recent detections (last 100)
                if (recentDetections.size() > 100) {
                    recentDetections.remove(0);
                }

                logger.info("Train detected: {}", detection);
            }

        } catch (Exception e) {
            logger.error("Error processing audio data: {}", e.getMessage());
        }
    }

    public List<TrainDetection> getRecentDetections() {
        return new CopyOnWriteArrayList<>(recentDetections);
    }

    public boolean isRunning() {
        return isRunning;
    }
}