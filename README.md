# Food Pantry

Food Pantry is an app designed to help users manage their pantry and create meal plans according to their current dietary needs and inventory. When the app is first started, users will be greeted with a login page unless they are already logged into their account. After signing in, users will be greeted with a custom main page that allows them to access other features of the app. The Pantry page is where users can input their current inventory and update it as needed. The Daily Nutrition page is where users can log their nutrition and share their nutrition plan with other users. The Recipes page is where users can search, save, and share recipes with other users. These recipes can be added to their generated meal plan. Users can save search preferences in the settings page.

## External Requirements

In order to build this project, you first have to install

* [Android Studio](https://developer.android.com/studio)

## Setup

Because this project uses Firebase modules, you must make and add the app to a project in the [Firebase Console](https://console.firebase.google.com/).

The required modules include
* Authentication, with Email/Password enabled
* Cloud Firestore

## Running

Press run in Android Studio.

# Deployment

An .apk will be built with Android Studio and released on Github for testers. 

# Testing

Instrumented Tests: (Location - Buggy/code/app/src/androidTest/com/example/buggy)
1. Create Account - Tests the ability to create a new account.
2. Login - Tests the ability to login with a valid and invalid account.
3. Navigation Bar - Tests the ability to go to different pages using the navigation bar.
4. Recipe - Tests the ability to go from the recipe page to the allergies and recipe search page.

Unit Tests: (Location - Buggy/code/app/src/test/com/example/buggy)
1. Allergies
2. Dietary Restrictions
3. Food Items
4. Recipe
5. User
   
## Running Tests

Instrumented Tests:
In Android Studio right click on the androidTest/com/example/buggy directory and click Run 'Tests in 'com.exampl...' or Ctrl+Shift+F10

Unit Tests:
In Android Studio right click on the test/com/example/buggy directory and click Run 'Tests in 'com.exampl...' or Ctrl+Shift+F10

# Authors
Jayden Allen jaydena@email.sc.edu
Avery Cronin awcronin@email.sc.edu
Jackie Dinh kdinh@email.sc.edu
Saleem Grich sgrich@email.sc.edu
Riley Haywood rhaywood@email.sc.edu
Added this change in Jayden branch.
