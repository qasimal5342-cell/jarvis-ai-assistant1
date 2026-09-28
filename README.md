# JARVIS Complete Android — Voice + Screen Control Foundation

This is the final Android project structure for a practical JARVIS-style assistant. It includes:

- Voice commands using Android SpeechRecognizer
- Text-to-speech replies
- Android Accessibility Service
- Tap, type, back, home, undo/redo-by-label
- Screen capture permission and foreground capture service
- Basic command router for Canva/YouTube/Google/GitHub and search
- Safe handling of destructive commands
- No paid SDK required for the included local features

## Important
A generic Android app cannot reliably redesign arbitrary Canva/AI-tool screens without an AI vision/planning backend. The project therefore exposes the Android control/vision foundation, while the optional vision planner must be connected to an AI endpoint. Do not treat the offline command parser as a full autonomous agent.

## Build
Open this folder in Android Studio. Sync Gradle and build the debug APK.

## First-run permissions
1. Microphone permission.
2. Settings → Accessibility → JARVIS → enable.
3. In JARVIS tap Enable Screen Vision and approve screen capture.

## Example commands
- open Canva
- open YouTube
- search product design ideas
- back
- home
- undo
- redo
- tap 500,800
- type hello
- screenshot

## Safety
Accessibility gives powerful control. Only enable JARVIS on a device you control. Do not grant accessibility access to untrusted builds.
