"Personal Security via Voice Recognition and Pitch Detection Android App" 

```markdown
# Personal Security via Voice Recognition and Pitch Detection Android App

This Android application enhances personal security by using voice recognition and pitch detection to automatically record videos and send them via email when a threat-like situation is detected. The app also shares the user's live location during emergency conditions.

## Features

- **Voice-Activated Recording**: Starts recording a video when a specific keyword or a scared voice is detected.
- **Automatic Email Sending**: Sends the recorded video to a pre-configured email address without any manual intervention.
- **Live Location Sharing**: Shares the user's current location in real-time during an emergency.
- **Customizable Settings**: Allows users to configure settings like keyword sensitivity, email address, and location sharing options.
- **Background Service**: The app runs in the background to monitor for voice activation commands.
- **Battery Optimization**: Optimized to minimize battery consumption during continuous monitoring and recording.

## Technologies Used

- **Android Studio**: The development environment used for building the app.
- **Java**: The primary programming language used.
- **XML**: Used for designing UI elements and layout.
- **MediaRecorder**: Utilized to record video.
- **SpeechRecognizer & AudioRecord**: Used for keyword and pitch detection.
- **JavaMail API**: For sending videos via email.
- **Location Services**: To fetch and send the user's live location.

## Getting Started

### Prerequisites

- Android Studio installed on your machine.
- An Android device or emulator to test the app.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/aakashbhamnia/-Personal-Security-via-Voice-Recognition-and-Pitch-Detection-Android-App-.git
   ```
2. Open the project in Android Studio.
3. Build the project and run it on an Android device or emulator.

### File Structure

- **MainActivity**: Controls the video recording process and manages the core functionalities.
- **SettingsActivity**: Allows users to configure app settings like email address and keywords.
- **VideoRecordingActivity**: Handles video recording using the `MediaRecorder` class.
- **EmailUtility**: Manages sending emails with the recorded video attached using the JavaMail API.
- **KeywordDetectionService**: Monitors the user's voice to detect predefined keywords or abnormal pitch levels.
- **PitchDetectionService**: Detects the user's voice pitch and triggers recording when the pitch suggests stress or fear.

## How It Works

1. **Voice Activation**: The app starts recording when it detects a pre-configured keyword or a scared voice.
2. **Video Recording**: A short video is recorded, which can be used as evidence or a safety measure.
3. **Email Transmission**: The recorded video is automatically sent to a predefined email address using the JavaMail API.
4. **Live Location**: Along with the video, the app sends the user’s current location in case of an emergency.

## Future Scope

- **Voice Assistant Integration**: Expanding functionality to integrate with popular voice assistants.
- **Machine Learning**: Incorporating ML algorithms for more accurate pitch and emotion detection.
- **Cloud Storage**: Adding support for cloud storage platforms like Google Drive or Dropbox for video uploads.
- **Real-time Streaming**: Implementing real-time video streaming capabilities.
- **Cross-Platform Compatibility**: Expanding the app to other platforms such as iOS.
E](LICENSE) file for details.

```

This README outlines the main features, technologies, installation steps, and future scope for the project. Let me know if you want to adjust anything!
