# Implementation Plan - Responsive UI across Phone Screen Sizes

Ensure all screens and forms in UniIntern are fully responsive, scrollable, and correctly handle system insets and keyboards on diverse physical phone screen sizes.

## User Review Required

> [!IMPORTANT]
> - All form containers will be verified to include `verticalScroll(rememberScrollState())`, `imePadding()`, `statusBarsPadding()`, and `navigationBarsPadding()`.
> - Fixed height/width constraints will be replaced with flexible modifiers (`fillMaxWidth()`, `wrapContentHeight()`, `weight()`).

## Proposed Changes

### [UI Layout & Responsiveness Audits]
#### [MODIFY] [LoginScreen.kt](file:///C:/Users/COMLAB1/Music/UniversityIntershipApp/app/src/main/java/com/example/universityintershipapp/LoginScreen.kt)
- Ensure scrollability and keyboard padding.

#### [MODIFY] [RegisterScreen.kt](file:///C:/Users/COMLAB1/Music/UniversityIntershipApp/app/src/main/java/com/example/universityintershipapp/RegisterScreen.kt)
- Ensure scrollability, keyboard padding, and flexible inputs.

#### [MODIFY] [PostInternshipScreen.kt](file:///C:/Users/COMLAB1/Music/UniversityIntershipApp/app/src/main/java/com/example/universityintershipapp/PostInternshipScreen.kt)
- Ensure form vertical scrolling and `imePadding()`.

#### [MODIFY] [SubmitApplicationScreen.kt](file:///C:/Users/COMLAB1/Music/UniversityIntershipApp/app/src/main/java/com/example/universityintershipapp/SubmitApplicationScreen.kt)
- Ensure scrollability and responsive document cards.

## Verification Plan

### Automated Tests
- Gradle build test (`app:assembleDebug`)
- Unit tests (`app:testDebugUnit`)
