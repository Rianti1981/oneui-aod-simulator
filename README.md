# One UI AOD Simulator

A small Android clock-screen simulator inspired by Samsung One UI Always On Display.

## Included
- Black background with a white digital clock
- Date and day display
- Blue accent styling
- Dimmed app-window brightness
- 10-second inactivity timeout; tapping the visible screen restarts the timer
- GitHub Actions workflow to build a debug APK

## Important device limitation
This is an app-based visual simulator, not Samsung's system AOD. It does not add AMOLED-style always-on capability to an LCD panel and cannot guarantee waking the display from a fully powered-off state with one tap. The 10-second timer closes the activity; Android then returns to the previous system screen.

## Build
Open the repository's **Actions** tab and run **Build One UI AOD APK**, or push a commit to the main branch. Download the APK from the workflow run's Artifacts section.
