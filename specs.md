## Prompt: Build Complete Workout Android App (Kotlin + Jetpack Compose)

Build a **production-ready Workout / Fitness Android App** using **Kotlin + Jetpack Compose** in **Android Studio**.

The app should follow **MVVM + Clean Architecture + Repository Pattern**.

Focus on clean, scalable, reusable, maintainable code.

---

# Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM Architecture
- Clean Architecture
- Hilt (Dependency Injection)
- Retrofit (API calls)
- OkHttp
- Kotlin Coroutines + Flow
- Room Database
- DataStore
- Coil (Image loading)
- Navigation Compose
- ViewModel
- StateFlow
- Paging 3
- WorkManager
- Firebase Auth (optional)
- Firebase FCM (notifications)
- ExoPlayer (exercise video)
- Lottie Animations
- Shimmer loading
- Accompanist
- Timber Logger

---

# Backend Integration

Use custom Node.js backend REST APIs.

Also integrate ExerciseDB API data through backend.

Base URL:

```kotlin
https://your-api.com/api/
```

---

# App Structure

Use modular & scalable folder structure:

```bash
app/
 ┣ core/
 ┃ ┣ network/
 ┃ ┣ utils/
 ┃ ┣ common/
 ┃ ┣ components/
 ┃ ┣ datastore/
 ┃ ┗ navigation/
 ┣ data/
 ┃ ┣ remote/
 ┃ ┣ local/
 ┃ ┣ repository/
 ┃ ┣ dto/
 ┃ ┗ mapper/
 ┣ domain/
 ┃ ┣ model/
 ┃ ┣ repository/
 ┃ ┗ usecase/
 ┣ presentation/
 ┃ ┣ auth/
 ┃ ┣ home/
 ┃ ┣ workouts/
 ┃ ┣ exercise/
 ┃ ┣ training/
 ┃ ┣ account/
 ┃ ┗ components/
```

---

# Screens to Build

## 1. Splash Screen

- App logo
- Smooth Lottie animation
- Navigate to Login/Home based on auth state

---

## 2. Authentication Module

### Login Screen

- Email
- Password
- Show/Hide password
- Forgot password
- Login button
- Google Sign In
- Register navigation

### Register Screen

- Name
- Email
- Phone
- Password
- Confirm password
- Height
- Weight
- Fitness goal
- Register button

### Features

- JWT login
- Save token in DataStore
- Form validation
- Loading state
- Error handling

---

# 3. Home Screen

Show:

- Welcome header
- Search bar
- Daily workout suggestions
- Featured workouts
- Recently viewed exercises
- Popular body part workouts
- Progress card
- Calories burned
- Quick chips:
  - Chest
  - Legs
  - Back
  - Arms
  - Cardio

Use:

- LazyColumn
- LazyRow
- Material cards
- Shimmer loading

---

# 4. My Workout Page

(Custom Workout)

Features:

- List saved workouts
- Create workout FAB
- Delete workout
- Edit workout
- Favorite workout
- Empty state UI

Use:

- Swipe actions
- Cards
- Pull-to-refresh

---

## Workout Detail Page

- Workout image
- Workout title
- Difficulty
- Duration
- Exercise count
- Sets / Reps / Rest
- Exercise list
- Start workout button
- Progress tracking

---

# 5. Exercise Library Page

Features:

- Search exercise
- Pagination
- Pull-to-refresh
- Filter:
  - Body part
  - Equipment
  - Target muscle
  - Difficulty

Show:

- Exercise image
- Exercise title
- Equipment
- Target muscle

Use:

- Paging 3
- LazyVerticalGrid

---

## Exercise Detail Page

Show:

- Large image / GIF
- Video player (ExoPlayer)
- Name
- Body part
- Target muscle
- Equipment
- Description
- Instructions
- Benefits
- Common mistakes
- Favorite button

---

# 6. Body Part Training Page

Grid layout:

- Chest
- Back
- Arms
- Shoulder
- Legs
- Abs
- Full Body
- Cardio

Each card:

- Image
- Level
- Duration
- Calories estimate

Click → Open training detail.

---

# 7. Training Plan Detail

Show:

- Cover image
- Title
- Level
- Calories
- Workout duration
- Exercise list
- Start plan

---

# 8. Workout Session Screen

Features:

- Timer
- Exercise progress
- Next exercise
- Previous exercise
- Rest timer
- Mark complete
- Finish workout

Use:

- Circular progress
- Animated transitions

---

# 9. Account Screen

Show:

- Profile photo
- Name
- Height / Weight
- BMI
- Fitness goal
- Workout history
- Saved workouts
- Dark mode toggle
- Notifications toggle
- Logout
- Delete account

---

# Navigation

Use Navigation Compose.

Bottom Navigation:

1. Home
2. My Workout
3. Exercise Library
4. Training
5. Account

Nested navigation:

- Auth graph
- Main graph
- Detail screens

---

# Local Storage

Use Room DB:

- Cached exercises
- Favorite exercises
- Favorite workouts
- Recently viewed
- Offline workouts

Use DataStore:

- JWT token
- Theme preference
- Notification settings

---

# API Layer

Use Retrofit + OkHttp.

Implement:

- Interceptors
- Token refresh
- Logging
- Error parser
- Safe API calls
- Retry handling

---

# State Management

Use:

- ViewModel
- StateFlow
- UiState sealed classes
- Loading / Success / Error state

---

# Reusable Components

Build reusable Compose components:

- PrimaryButton
- SearchBar
- WorkoutCard
- ExerciseCard
- EmptyState
- ErrorView
- LoadingShimmer
- TopBar
- BottomBar
- ProgressCard
- FilterChip
- InputField

---

# UI/UX Requirements

Use modern premium UI:

- Material 3
- Rounded cards
- Dark theme
- Smooth animations
- High readability
- Responsive layout
- Consistent spacing
- Nike Training Club / Fitbod inspired

---

# Performance

Implement:

- Pagination
- Image caching
- Lazy loading
- Offline support
- Debounced search
- Efficient recomposition
- Stable state handling

---

# Security

- Secure token storage
- Input validation
- Network timeout handling
- Session expiration
- Logout cleanup

---

# Testing

Write:

- Unit tests
- ViewModel tests
- Repository tests
- UI Compose tests

---

# Deliverables

Generate:

- Full Android Studio project
- Production-ready Kotlin code
- MVVM clean architecture
- Hilt setup
- Retrofit setup
- Room DB setup
- Navigation setup
- Reusable components
- API integration
- Error handling
- Clean comments
- Best practices

---

## Build rule

Write **complete code step by step module-wise**, starting with:

1. Project setup
2. Dependencies
3. Folder structure
4. Core module
5. Data layer
6. Domain layer
7. Presentation layer
8. Navigation
9. Feature screens
10. API integration
11. Local database
12. Final optimization



For Design reference you can see this stitch_elite_fitness_compose_ui folder there you will find the design for each pages, make exactly like this, use the bottom nav view for navigating between the pages.

