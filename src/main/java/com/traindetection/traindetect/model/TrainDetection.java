package com.traindetection.traindetect.model;

import java.time.LocalDateTime;

public class TrainDetection {
    private LocalDateTime timestamp;
    private TrainType trainType;
    private double confidence;
    private double[] frequencyProfile;
    private double amplitude;

    public enum TrainType {
        BART_TRAIN,
        GOODS_TRAIN,
        UNKNOWN
    }

    public TrainDetection(TrainType trainType, double confidence, double amplitude) {
        this.timestamp = LocalDateTime.now();
        this.trainType = trainType;
        this.confidence = confidence;
        this.amplitude = amplitude;
    }

    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public TrainType getTrainType() { return trainType; }
    public void setTrainType(TrainType trainType) { this.trainType = trainType; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public double[] getFrequencyProfile() { return frequencyProfile; }
    public void setFrequencyProfile(double[] frequencyProfile) { this.frequencyProfile = frequencyProfile; }

    public double getAmplitude() { return amplitude; }
    public void setAmplitude(double amplitude) { this.amplitude = amplitude; }

    @Override
    public String toString() {
        return String.format("TrainDetection{timestamp=%s, type=%s, confidence=%.2f, amplitude=%.2f}",
                timestamp, trainType, confidence, amplitude);
    }
}