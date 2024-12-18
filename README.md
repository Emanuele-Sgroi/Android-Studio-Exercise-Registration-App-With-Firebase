# Android Studio Exercise: Registration With Firebase
This is a simple Android application built with Java and Firebase in Android Studio. The app allows users to register, log in, and view their profile information on a dashboard.
## How it works
### Firebase integration:
- User registration data stored securely in Firestore.
- Checks for duplicate usernames or emails during registration.
- Retrieves user information on successful login.
### Registration Screen:
- Inputs for full name, username, email, password, repeat password, sex (radio buttons), and date of birth (date picker).
- Validations include:
  - Required fields.
  - Email format check.
  - Password strength requirements.
  - Minimum age of 18.
  - Validation for credentials against Firebase Firestore.
- Reset form functionality.
### Login Screen:
- Input for username or email and password.
- Validation for credentials against Firebase Firestore.
- Displays error messages for invalid inputs or incorrect credentials.
### Dashboard Screen:
- Displays the user's full name, username, email, sex, date of birth, and calculated age.
- Log out functionality.

***Created for academic purposes.***
