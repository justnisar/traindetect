# Train Detection System

A real-time audio-based train detection and classification system built with Spring Boot. This application captures audio from the system microphone, processes it using FFT analysis, and classifies detected trains as BART trains or goods trains based on their acoustic signatures.

## Features

- **Real-time Audio Capture**: Continuously monitors audio input from system microphone
- **Signal Processing**: Implements FFT analysis with Hamming windowing for frequency domain analysis
- **Train Classification**: Distinguishes between BART trains and goods trains based on frequency characteristics
- **REST API**: Provides endpoints for monitoring detections and controlling the system
- **Configurable Parameters**: Adjustable audio processing and detection thresholds
- **Spring Boot Actuator**: Built-in health checks and metrics

## Architecture

The system consists of several key components:

- **AudioCaptureService**: Handles real-time audio capture from the system microphone
- **SignalProcessingService**: Performs FFT analysis, windowing, and frequency band extraction
- **TrainClassificationService**: Classifies trains based on frequency patterns
- **TrainDetectionService**: Orchestrates the detection pipeline and manages results
- **REST Controller**: Provides API endpoints for system interaction

## Technical Details

### Audio Processing Pipeline

1. **Audio Capture**: 16-bit mono audio at 44.1kHz sample rate
2. **Windowing**: Hamming window applied to reduce spectral leakage
3. **FFT Analysis**: 1024-point FFT for frequency domain conversion
4. **Frequency Band Analysis**: Extracts energy in three bands:
   - Low (20-200 Hz): Track rumble and wheel noise
   - Mid (200-2000 Hz): Engine and brake sounds
   - High (2000-8000 Hz): Wheel squealing and air brakes

### Classification Logic

- **BART Trains**: Characterized by higher frequency content from electric motors and distinctive braking sounds
- **Goods Trains**: Identified by strong low-frequency rumble from heavy cargo and diesel engine noise

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- System with audio input capabilities (microphone)
- Operating system with Java Sound API support

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd traindetect
```

2. Build the project:
```bash
mvn clean package
```

3. Run the application:
```bash
java -jar target/traindetect-1.0.0.jar
```

Or use Maven:
```bash
mvn spring-boot:run
```

## Configuration

The application can be configured through `application.yml`:

```yaml
audio:
  sample-rate: 44100.0          # Audio sample rate (Hz)
  sample-size-in-bits: 16       # Bit depth
  channels: 1                   # Mono audio
  buffer-size: 4096             # Audio buffer size
  fft-size: 1024               # FFT window size
  noise-threshold: 0.05         # Minimum amplitude threshold
  train-detection-threshold: 0.2 # Minimum confidence for detection
```

## API Endpoints

### Detection Management
- `POST /api/trains/start` - Start train detection
- `POST /api/trains/stop` - Stop train detection
- `GET /api/trains/status` - Get system status

### Data Retrieval
- `GET /api/trains/detections` - Get recent train detections

### Health Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/metrics` - System metrics

## Usage Examples

### Start Detection
```bash
curl -X POST http://localhost:8080/api/trains/start
```

### Get Recent Detections
```bash
curl http://localhost:8080/api/trains/detections
```

### Check System Status
```bash
curl http://localhost:8080/api/trains/status
```

### Sample Detection Response
```json
[
  {
    "timestamp": "2025-06-09T10:30:45.123",
    "trainType": "BART_TRAIN",
    "confidence": 0.75,
    "amplitude": 0.42,
    "frequencyProfile": [0.15, 0.35, 0.62]
  }
]
```

## Dependencies

- **Spring Boot 3.5.0**: Application framework
- **Spring Boot Starter Web**: REST API support
- **Spring Boot Starter Actuator**: Health monitoring
- **JTransforms 3.1**: FFT implementation for signal processing
- **Java Sound API**: Audio capture (built into JDK)

## Development

### Running in Development Mode
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Building for Production
```bash
mvn clean package -Pproduction
```

## Troubleshooting

### Common Issues

1. **No Audio Device Found**
   - Ensure microphone is connected and recognized by the system
   - Check system audio permissions

2. **Poor Detection Accuracy**
   - Adjust `noise-threshold` and `train-detection-threshold` in configuration
   - Ensure microphone is positioned to capture train sounds effectively

3. **High CPU Usage**
   - Reduce `buffer-size` or increase processing intervals
   - Consider running on dedicated hardware for production use

### Logging

Enable debug logging for audio processing:
```yaml
logging:
  level:
    com.traindetection: DEBUG
```

## Performance Considerations

- The system processes audio in real-time, requiring sufficient CPU resources
- Memory usage scales with the number of stored detections (limited to 100 recent detections)
- Network latency may affect real-time API responses during heavy processing

## Future Enhancements

- Machine learning-based classification for improved accuracy
- Database persistence for long-term detection history
- WebSocket support for real-time detection streaming
- Multiple audio source support
- Advanced noise filtering and environmental adaptation

## Acknowledgments

- JTransforms library for efficient FFT implementation
- Spring Boot team for the excellent framework
- Audio processing techniques inspired by digital signal processing literature
