package com.traindetection.traindetect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "audio")
public class AudioConfig {
    private float sampleRate = 44100.0f;
    private int sampleSizeInBits = 16;
    private int channels = 1;
    private boolean signed = true;
    private boolean bigEndian = false;
    private int bufferSize = 4096;
    private int fftSize = 1024;
    private double noiseThreshold = 0.1;
    private double trainDetectionThreshold = 0.3;

    // Getters and setters
    public float getSampleRate() { return sampleRate; }
    public void setSampleRate(float sampleRate) { this.sampleRate = sampleRate; }

    public int getSampleSizeInBits() { return sampleSizeInBits; }
    public void setSampleSizeInBits(int sampleSizeInBits) { this.sampleSizeInBits = sampleSizeInBits; }

    public int getChannels() { return channels; }
    public void setChannels(int channels) { this.channels = channels; }

    public boolean isSigned() { return signed; }
    public void setSigned(boolean signed) { this.signed = signed; }

    public boolean isBigEndian() { return bigEndian; }
    public void setBigEndian(boolean bigEndian) { this.bigEndian = bigEndian; }

    public int getBufferSize() { return bufferSize; }
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }

    public int getFftSize() { return fftSize; }
    public void setFftSize(int fftSize) { this.fftSize = fftSize; }

    public double getNoiseThreshold() { return noiseThreshold; }
    public void setNoiseThreshold(double noiseThreshold) { this.noiseThreshold = noiseThreshold; }

    public double getTrainDetectionThreshold() { return trainDetectionThreshold; }
    public void setTrainDetectionThreshold(double trainDetectionThreshold) {
        this.trainDetectionThreshold = trainDetectionThreshold;
    }
}