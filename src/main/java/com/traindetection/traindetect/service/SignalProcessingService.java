package com.traindetection.traindetect.service;

import com.traindetection.traindetect.config.AudioConfig;
import org.jtransforms.fft.DoubleFFT_1D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignalProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(SignalProcessingService.class);

    @Autowired
    private AudioConfig audioConfig;

    private DoubleFFT_1D fft;

    public double[] bytesToDoubles(byte[] audioData) {
        double[] samples = new double[audioData.length / 2];

        for (int i = 0; i < samples.length; i++) {
            int sample = ((audioData[i * 2 + 1] & 0xFF) << 8) | (audioData[i * 2] & 0xFF);
            if (sample > 32767) sample -= 65536; // Convert to signed
            samples[i] = sample / 32768.0; // Normalize to [-1, 1]
        }

        return samples;
    }

    public double[] computeFFT(double[] samples) {
        int fftSize = Math.min(audioConfig.getFftSize(), samples.length);
        double[] fftInput = new double[fftSize * 2]; // Real and imaginary parts

        // Copy samples and pad with zeros if necessary
        System.arraycopy(samples, 0, fftInput, 0, Math.min(fftSize, samples.length));

        if (fft == null ) {
            fft = new DoubleFFT_1D(fftSize);
        }

        fft.realForwardFull(fftInput);

        // Calculate magnitude spectrum
        double[] magnitude = new double[fftSize / 2];
        for (int i = 0; i < magnitude.length; i++) {
            double real = fftInput[i * 2];
            double imag = fftInput[i * 2 + 1];
            magnitude[i] = Math.sqrt(real * real + imag * imag);
        }

        return magnitude;
    }

    public double calculateRMS(double[] samples) {
        double sum = 0;
        for (double sample : samples) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / samples.length);
    }

    public double[] applyHammingWindow(double[] samples) {
        double[] windowed = new double[samples.length];
        int N = samples.length;

        for (int i = 0; i < N; i++) {
            double w = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (N - 1));
            windowed[i] = samples[i] * w;
        }

        return windowed;
    }

    public double[] extractFrequencyBands(double[] spectrum, int sampleRate) {
        // Extract specific frequency bands relevant to train detection
        // Low frequency: 20-200 Hz (rumble, wheels on tracks)
        // Mid frequency: 200-2000 Hz (engine noise, brakes)
        // High frequency: 2000-8000 Hz (wheel squealing, air brakes)

        double[] bands = new double[3];
        int spectrumLength = spectrum.length;
        double freqPerBin = (double) sampleRate / (2 * spectrumLength);

        // Low band: 20-200 Hz
        int lowStart = (int) (20 / freqPerBin);
        int lowEnd = (int) (200 / freqPerBin);
        for (int i = lowStart; i < Math.min(lowEnd, spectrumLength); i++) {
            bands[0] += spectrum[i];
        }
        bands[0] /= (lowEnd - lowStart);

        // Mid band: 200-2000 Hz
        int midStart = (int) (200 / freqPerBin);
        int midEnd = (int) (2000 / freqPerBin);
        for (int i = midStart; i < Math.min(midEnd, spectrumLength); i++) {
            bands[1] += spectrum[i];
        }
        bands[1] /= (midEnd - midStart);

        // High band: 2000-8000 Hz
        int highStart = (int) (2000 / freqPerBin);
        int highEnd = (int) (8000 / freqPerBin);
        for (int i = highStart; i < Math.min(highEnd, spectrumLength); i++) {
            bands[2] += spectrum[i];
        }
        bands[2] /= (highEnd - highStart);

        return bands;
    }
}