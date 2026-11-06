# One UI AOD Simulator — Root Diagnostic Edition

Android app for Samsung-style clock preview and read-only SukiSU root diagnostics.

## Features
- One UI-inspired black clock preview with date and blue accent
- Preview closes after 10 seconds
- **Periksa akses ROOT** runs `su -c id` and reads basic device properties
- **Periksa dukungan touch wake** reads input device names and wakeup flags
- Does not write to kernel nodes, thermal controls, or system settings
- GitHub Actions builds a debug APK automatically

## Device limitation
The Samsung Galaxy A06 5G uses an LCD panel; this app is not Samsung's native AOD and cannot create true always-on display while the panel is off. A single tap from a fully off display requires touchscreen-driver and kernel support. The diagnostic only reports available read-only information; a wakeup flag alone does not guarantee tap-to-wake.

## Build
Open **Actions** and download the `OneUI-AOD-Simulator-debug` artifact from the latest successful run.
