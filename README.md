# Weather with LLM

This project is an Android application that allows users to explore weather insights for multiple cities, personalize their experience through UI customization, and utilize AI-driven features for dynamic weather insights.

---

## Features

### Implemented Features
1. **User Authentication**:
   - Sign-up and login functionality for multiple users with personalized UI themes.
   - Logout functionality that redirects to the authentication screen.

2. **City Management**:
   - Add and remove cities from a personalized list for each user.
   - Persist user-specific lists using Android's Content Providers.

3. **Weather Insights**:
   - Fetch weather details for selected cities.
   - Use Large Language Models (LLMs) to provide context-relevant weather insights.

4. **Maps Integration**:
   - Display maps for selected cities.
   - Mock location data for testing purposes.

5. **UI Customization**:
   - Tailor themes and layouts to user preferences during sign-up.

6. **Testing**:
   - Instrumented tests using Espresso for core functionalities.
   - LLM-generated test cases for authentication.

---

## Milestones

### Team Setup and Project Initialization
- Formed the team and set up a private GitHub repository.
- Configured the development environment with Android Studio and an emulator.

### Requirement Engineering
- Documented informal requirements and created fully dressed use cases.
- Designed the UML class diagram and Component Transition Graph.

### Feature Implementation
- Implemented user authentication and city management features.
- Enhanced UI with user-specific customization.
- Google API and Gemini Integration

### Milestone 5: Validation and Testing
- Created and executed LLM-generated test cases for login and signup functionalities.
- Developed instrumented tests for feature validation using Espresso.

---

## Folder Structure
```plaintext
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle
├── videos/
│   ├── feature-demo.mp4
├── docs/
│   ├── requirements.pdf
│   ├── implementation.pdf
│   ├── testing.pdf
├── README.md
```

---

## Installation and Setup

1. **Install Android Studio**:
   - [Download and Install Android Studio](https://developer.android.com/studio).

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/ChandanaGiridhar/Weather_with_LLM.git
   cd Weather_with_LLM
   ```

3. **Run the Application**:
   - Open the project in Android Studio.
   - Sync Gradle and build the project.
   - Launch the emulator and run the app.

---

## Usage

1. **Sign Up**:
   - Enter a username, password, and select a theme.
   - Login to manage your list of cities.

2. **Add/Remove Cities**:
   - Use the "Add a Location" button to add cities.
   - Remove cities by selecting the delete option.

3. **Weather Insights**:
   - Click a city to view weather information and AI-generated insights.

4. **Map View**:
   - View city locations on a map.

---

## Testing

- **Instrumented Tests**: Located under `app/src/androidTest/`.
- **LLM-Generated Test Cases**: Scripts and reports for LLM feature.





