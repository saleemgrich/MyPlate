# Food Pantry

Food Pantry is an app designed to help users manage their pantry and create meal plans according to their current dietary needs and inventory. When the app is first started, users will be greeted with a login page unless they are already logged into their account. After creating their account or signing in, users will be greeted with the pantry page. The Pantry page is where users can input their current inventory and update it as needed. The Daily Nutrition page is where users can create meal plans for any given day. The Recipes page is where users can search for and save recipes. Users can save search preferences and dietary restrictions here. The Friends page allows the user to add friends and click on their friends to see their saved recipes. The settings page allows users to change their username, password, email, and phone number. They can also sign out and delete their account from here.

## External Requirements

In order to build this project, you first have to install

* [Android Studio](https://developer.android.com/studio)
* Install Android Emulator

## Setup

Clone Repository

## Running

Press run in Android Studio (LoginActivity.kt).

# Deployment

An .apk will be built with Android Studio and released on Github for testers. 

# Testing

Instrumented Tests: (Location - MyPlate/code/app/src/androidTest/com/example/buggy)
1. Create Account - Tests the ability to create a new account.
2. Login - Tests the ability to login with a valid and invalid account.
3. Navigation Bar - Tests the ability to go to different pages using the navigation bar.
4. Recipe - Tests the ability to go from the recipe page to the allergies and recipe search page.

Unit Tests: (Location - MyPlate/code/app/src/test/com/example/buggy)
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
Saleem Grich sgrich@email.sc.edu
