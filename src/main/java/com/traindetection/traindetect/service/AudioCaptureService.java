package com.traindetection.traindetect.service;

import com.traindetection.traindetect.config.AudioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class AudioCaptureService {
    private static final Logger logger = LoggerFactory.getLogger(AudioCaptureService.class);

    @Autowired
    private AudioConfig audioConfig;

    private TargetDataLine targetDataLine;
    private BlockingQueue<byte[]> audioBuffer = new LinkedBlockingQueue<>();
    private volatile boolean isCapturing = false;

    public void startCapture() {
        if (isCapturing) {
            logger.warn("Audio capture is already running");
            return;
        }

        try {
            AudioFormat format = new AudioFormat(
                    audioConfig.getSampleRate(),
                    audioConfig.getSampleSizeInBits(),
                    audioConfig.getChannels(),
                    audioConfig.isSigned(),
                    audioConfig.isBigEndian()
            );

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                logger.error("Audio line not supported");
                return;
            }

            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format, audioConfig.getBufferSize());
            targetDataLine.start();

            isCapturing = true;
            logger.info("Started audio capture with format: {}", format);

            // Start capture thread
            Thread captureThread = new Thread(this::captureLoop);
            captureThread.setDaemon(true);
            captureThread.start();

        } catch (LineUnavailableException e) {
            logger.error("Error starting audio capture: {}", e.getMessage());
        }
    }

    public void stopCapture() {
        isCapturing = false;
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
            logger.info("Stopped audio capture");
        }
    }

    private void captureLoop() {
        byte[] buffer = new byte[audioConfig.getBufferSize()];

        while (isCapturing) {
            try {
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    byte[] audioData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, audioData, 0, bytesRead);

                    if (!audioBuffer.offer(audioData)) {
                        logger.warn("Audio buffer full, dropping audio data");
                    }
                }
            } catch (Exception e) {
                logger.error("Error in audio capture loop: {}", e.getMessage());
                break;
            }
        }
    }

    public byte[] getNextAudioData() throws InterruptedException {
        return audioBuffer.take();
    }

    public boolean hasAudioData() {
        return !audioBuffer.isEmpty();
    }

    public boolean isCapturing() {
        return isCapturing;
    }
}