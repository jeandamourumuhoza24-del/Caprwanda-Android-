# CapRwanda - Mobile Video Editor

**CapRwanda** is a complete, production-ready Android mobile video editor inspired by CapCut, designed for creating high-quality video reels, vlogs, and traditional cultural clips in Rwanda and beyond.

---

## 🌟 Key Features

1. **Home Screen & Templates**
   - Recent projects list with local Room database persistence.
   - Trending Rwanda Video Templates (*Kigali Sunset Vlog*, *Intore Rhythm Beats*, *Volcanoes Park Trek*, *Lake Kivu Breeze*).

2. **Video Import**
   - Gallery video picker & Camera recording support.
   - Multi-video selection and aspect ratio customization (`9:16`, `16:9`, `1:1`, `4:5`).

3. **Multi-Track Timeline Editor**
   - Interactive multi-track timeline (Video, Text/Subtitles, Audio, Stickers).
   - Scrubber time bar and zoom scale controls (`0.5x` to `3.0x`).

4. **Editing Tools**
   - Trim, Split at current playhead, Cut, Rotate (`90°`), Flip horizontal, Speed control (`0.25x` to `4.0x`), Reverse clip, Duplicate, and Delete.

5. **Effects & Color Filters**
   - Color Presets (*Cinematic*, *Rwandan Dawn*, *Vibrant*, *Vintage Sepia*, *Noir B&W*, *Warm Sunset*).
   - Sliders for Brightness, Contrast, and Saturation adjustments.

6. **Transitions & Text**
   - Transitions: *Fade In*, *Slide Right*, *Zoom Punch*, *Dissolve Glow*, *Wipe Cross*.
   - Text Overlays: Animated titles, custom fonts, color palettes, and entrance animations.

7. **Audio & Voice Studio**
   - Music track library (Afrobeat, Traditional Rwandan Folk, Acoustic, Lo-Fi).
   - Microphone Voiceover recording studio with volume boost and fade effects.

8. **AI Smart Features**
   - **AI Auto Captions**: Generates synchronized subtitles in **Kinyarwanda**, **English**, and **French**.
   - **AI Subject Cutout**: Removes video background with subject segmentation.
   - **Chroma Key**: Green screen keying with tolerance controls.

9. **Export & Settings**
   - Export resolutions: **720p**, **1080p**, **4K** at 30 or 60 FPS in **MP4** format.
   - Real-time progress bar with frame counter and time remaining indicator.
   - Settings with Dark/Light theme toggle, Language switcher, and Cache cleaner.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 100%
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM + Repository Pattern
- **Local Database**: Room DB (KSP)
- **Media Playback**: Media3 / ExoPlayer
- **Navigation**: Jetpack Navigation Compose
- **Asynchrony**: Kotlin Coroutines & Flow

---

## 🚀 Building & Running

### Local Build
```bash
# Build Debug APK
./gradlew assembleDebug
```

### GitHub Actions
The project includes a `.github/workflows/build-debug-apk.yml` workflow that automatically builds the debug APK on pushes and pull requests.
