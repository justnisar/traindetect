package com.traindetection.traindetect.service;

import com.traindetection.traindetect.config.AudioConfig;
import com.traindetection.traindetect.model.TrainDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainClassificationService {
    private static final Logger logger = LoggerFactory.getLogger(TrainClassificationService.class);

    @Autowired
    private AudioConfig audioConfig;

    // Simplified classification based on frequency characteristics
    public TrainDetection classifyTrain(double[] frequencyBands, double amplitude) {
        double lowBand = frequencyBands[0];
        double midBand = frequencyBands[1];
        double highBand = frequencyBands[2];

        // Check if signal is above noise threshold
        if (amplitude < audioConfig.getNoiseThreshold()) {
            return null; // Below noise threshold
        }

        TrainDetection.TrainType trainType = TrainDetection.TrainType.UNKNOWN;
        double confidence = 0.0;

        // BART trains typically have:
        // - Higher frequency content due to electric motors
        // - Distinctive braking sounds (high frequency)
        // - Smoother acceleration patterns
        if (highBand > midBand && midBand > lowBand * 1.5) {
            trainType = TrainDetection.TrainType.BART_TRAIN;
            confidence = Math.min(0.9, (highBand / (lowBand + 0.1)) * 0.3);
        }
        // Goods trains typically have:
        // - Strong low-frequency rumble from heavy cargo
        // - Diesel engine noise (mid frequencies)
        // - Longer, steadier patterns
        else if (lowBand > midBand && lowBand > highBand * 2) {
            trainType = TrainDetection.TrainType.GOODS_TRAIN;
            confidence = Math.min(0.9, (lowBand / (highBand + 0.1)) * 0.2);
        }

        // Only return detection if confidence is above threshold
        if (confidence > audioConfig.getTrainDetectionThreshold()) {
            return new TrainDetection(trainType, confidence, amplitude);
        }

        return null;
    }

    // Method to update classification parameters based on observed patterns
    public void updateClassificationModel(TrainDetection detection, boolean confirmed) {
        // This could be extended to implement machine learning-based adaptation
        logger.debug("Classification feedback: {} confirmed={}", detection, confirmed);
    }
}