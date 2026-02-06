# Users API - Android App with Jetpack Compose

An Android application built with Jetpack Compose that displays user information using the [Random User API](https://randomuser.me/documentation). This app allows users to generate random user profiles based on custom filters and manage them with various actions.

##  Features

### Form Screen
- **User Count Input**: Enter the number of records to generate (3-10)
- **Nationality Selection**: Choose from 5 different nationalities using checkboxes
- **Information Types**: Select which user data to display:
  - Gender
  - Name
  - Location
  - Email
  - Photo (required)
- **Form Validation**: Real-time validation with error messages displayed in red
- **Generate Button**: Triggers the API call when validation passes

### Results Screen
- **Loading Animation**: Displays a centered loading spinner with "Încărcare înregistrări..." text for minimum 2 seconds
- **User List**: Vertical list displaying all selected user information
- **Action Menu**: Each user item has three options:
  - **Report Record**: Marks the record as reported (changes background to #FDD8D8)
  - **Save Record**: Marks the record as saved (changes background to #E4FAE4) and persists across searches
  - **Reset Record**: Resets the record to its original state
- **Confirmation Dialogs**: All actions require user confirmation

### Global Menu (AppBar)
- **Saved Records Only**: View all saved records from all searches
- **Query Results**: View the latest query results including reported and saved records

##  Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit
- **Data Persistence**: DataStore
- **Dependency Injection**: Hilt (if applicable)
- **Async Operations**: Kotlin Coroutines
- **API**: [Random User API](https://randomuser.me/)

##  Project Structure

```
app/src/main/java/com/example/usersapicompose/
├── data/
│   ├── api/
│   │   ├── RandomUserApi.kt          # Retrofit API interface
│   │   └── RetrofitClient.kt         # Retrofit client setup
│   ├── model/
│   │   ├── RandomUserModels.kt       # API response models
│   │   └── SavedUser.kt              # Saved user model
│   └── repo/
│       └── UsersRepository.kt        # Repository pattern implementation
├── datastore/
│   └── SavedUsersStore.kt            # DataStore for persistent storage
├── ui/
│   ├── components/                   # Reusable UI components
│   ├── screens/
│   │   ├── FormScreen.kt             # Form input screen
│   │   └── ResultsScreen.kt          # Results display screen
│   ├── theme/
│   │   ├── Color.kt                  # Color definitions
│   │   ├── Theme.kt                  # App theme
│   │   └── Type.kt                   # Typography
│   └── viewmodel/
│       └── UsersViewModel.kt         # ViewModel for state management
└── MainActivity.kt                   # Main activity
```

##  Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Kotlin 1.9+

##  Usage

1. **Launch the app** - You'll see the form screen
2. **Fill in the form**:
   - Enter a number between 3 and 10 for the record count
   - Select at least 2 nationalities
   - Select at least 3 information types
   - Photo selection is mandatory
3. **Click "Generează utilizatori"** - The app will validate your input
4. **View results** - After loading, see the generated user list
5. **Interact with records**:
   - Tap the menu icon on any user card
   - Choose to Report, Save, or Reset the record
6. **Navigate views**:
   - Use the AppBar menu to switch between Saved Records and Query Results

##  API Integration

The app integrates with the Random User API with the following parameters:
- `results`: Number of users to generate
- `nat`: Comma-separated nationality codes
- `inc`: Comma-separated fields to include

Example API call:
```
https://randomuser.me/api/?results=5&nat=us,gb,fr&inc=gender,name,location,email,picture
```

## 🎨 UI/UX Features

- **Material Design 3**: Modern UI following Material Design guidelines
- **Responsive Layout**: Adapts to different screen sizes
- **Color Coding**: Visual feedback for record states
  - Default: Original background
  - Reported: #FDD8D8 (light red)
  - Saved: #E4FAE4 (light green)
- **Confirmation Dialogs**: Prevents accidental actions
- **Error Handling**: User-friendly error messages

##  State Management

The app maintains different states:
- **Form State**: Current form inputs and validation errors
- **Loading State**: API call in progress
- **Results State**: Current query results with user actions
- **Saved Records State**: Persistent list of saved users across sessions

##  Validation Rules

- **Record Count**: Must be between 3 and 10
- **Nationalities**: At least 2 must be selected
- **Information Types**: At least 3 must be selected
- **Photo**: Must be selected (mandatory)

- Android Jetpack Compose team for the amazing UI framework

---

**Note**: This application was developed as part of the Android Development course 2025.
