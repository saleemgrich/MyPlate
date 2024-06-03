package com.example.buggy.network

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.buggy.data.appdata.Allergies
import com.example.buggy.data.appdata.AppUserData
import com.example.buggy.data.appdata.DatedRecipes
import com.example.buggy.data.appdata.DietaryRestrictions
import com.example.buggy.data.appdata.SavedRecipes
import com.example.buggy.data.appdata.TodaysRecipes
import com.example.buggy.data.model.DatedRecipe
import com.example.buggy.data.model.FoodItem
import com.example.buggy.data.model.Recipe
import com.example.buggy.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.Random

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val pantryCollection = db.collection("Pantry")
    private val friendCollection = db.collection("Friends")
    private val restrictionCollection = db.collection("Restrictions")
    private val allergiesCollection = db.collection("Allergies")
    private val recipeCollection = db.collection("Recipe")

    fun writeUser(user: User, onComplete: (Boolean, String?) -> Unit) {
        generateUserID { userID ->
            val temp = User(user.email, null, user.name, user.password,
                user.phoneNumber, userID, user.username)
            usersCollection.document(temp.email)
                .set(temp)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true, null) // Data write successful
                    } else {
                        onComplete(false, task.exception?.message) // Data write failed
                    }
                }
        }
    }

    fun readUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        usersCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null && user.password == password) {
                        AppUserData.currentUser = user
                        // Populate Dietary Restrictions and Allergies
                        getAllergies { allergies ->
                            Allergies.setAllergy(allergies)
                            Log.d(TAG, "Retrieved allergies")
                        }
                        getRestrictions { restrictions ->
                            DietaryRestrictions.setRestriction(restrictions)
                            Log.d(TAG, "Retrieved restrictions")
                        }
                        // Populate Saved Recipes
                        getRecipeList(user.userID) { recipes ->
                            SavedRecipes.recipes = recipes
                            if(!recipes.isNullOrEmpty()) {
                                recipes.get(0).title?.let { Log.d(TAG, it) }
                            }
                        }
                        // Populate Todays Recipes
                        getTodaysRecipeList(user.userID) { recipes ->
                            TodaysRecipes.recipes = recipes
                            if(!recipes.isNullOrEmpty()) {
                                recipes.get(0).title?.let { Log.d(TAG, it) }
                            }
                        }
                        // Populate Dated Recipes
                        getDatedRecipeList(user.userID) { recipes ->
                            DatedRecipes.recipes = recipes
                            if(!recipes.isNullOrEmpty()) {
                                recipes.get(0).title?.let { Log.d(TAG, it) }
                            }
                        }

                        onComplete(true, null) // Email and password match
                    } else {
                        onComplete(false, "Incorrect password") // Incorrect password
                    }
                } else {
                    onComplete(false, "User not found") // User document does not exist
                }
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message ?: "Failed to fetch user data") // Data read failed
            }
    }
    fun checkEmailExists(email: String, onComplete: (Boolean) -> Unit) {
        usersCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onComplete(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onComplete(false)
            }
    }

    fun checkUsernameExists(username: String, onComplete: (Boolean) -> Unit) {
        usersCollection.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onComplete(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                onComplete(false)
            }
    }

    fun changeEmail(context: Context, onComplete: (Boolean) -> Unit) {
        val currentUserEmail = AppUserData.currentUser?.email
        if (currentUserEmail != null) {
            usersCollection.whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        // Show an AlertDialog to prompt the user for a new email
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Change email")
                        builder.setMessage("Enter a new email")

                        val input = EditText(context)
                        builder.setView(input)

                        builder.setPositiveButton("OK") { _, _ ->
                            val newEmail = input.text.toString().lowercase(Locale.ROOT)
                            if (newEmail.isBlank()) {
                                Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                                onComplete(false)
                            } else {
                                checkEmailExists(newEmail) { exists ->
                                    if(exists) {
                                        Toast.makeText(context, "Email is already in use", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Update the email in Firestore
                                        documentSnapshot.reference.update("email", newEmail)
                                            .addOnSuccessListener {
                                                // Email updated successfully
                                                Toast.makeText(context, "New email: $newEmail", Toast.LENGTH_SHORT).show()
                                                onComplete(true)
                                            }
                                            .addOnFailureListener { exception ->
                                                // Failed to update email
                                                Toast.makeText(context, "Failed to update email: ${exception.message}", Toast.LENGTH_SHORT).show()
                                                onComplete(false)
                                            }
                                        AppUserData.currentUser?.email = newEmail
                                    }
                                }
                            }
                        }
                        builder.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                            onComplete(false)
                        }
                        builder.show()
                    } else {
                        // No user found with the current email
                        onComplete(false)
                    }
                }
                .addOnFailureListener { exception ->
                    // Error fetching user document
                    Toast.makeText(context, "Error fetching user document: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
        }
    }

    fun changeUsername(context: Context, onComplete: (Boolean) -> Unit) {
        val currentUserEmail = AppUserData.currentUser?.email
        if (currentUserEmail != null) {
            usersCollection.whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentSnapshot = querySnapshot.documents[0]
                        // Show an AlertDialog to prompt the user for a new username
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Change Username")
                        builder.setMessage("Enter a new username")

                        val input = EditText(context)
                        builder.setView(input)

                        builder.setPositiveButton("OK") { _, _ ->
                            val newUsername = input.text.toString()
                            if (newUsername.isBlank()) {
                                Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                                onComplete(false)
                            } else {
                                checkUsernameExists(newUsername) { exists ->
                                    if (exists) {
                                        Toast.makeText(context, "Username is already in use", Toast.LENGTH_SHORT).show()
                                    }else {
                                        // Update the username in Firestore
                                        documentSnapshot.reference.update("username", newUsername)
                                            .addOnSuccessListener {
                                                // Username updated successfully
                                                Toast.makeText(context, "New username: $newUsername", Toast.LENGTH_SHORT).show()
                                                onComplete(true)
                                            }
                                            .addOnFailureListener { exception ->
                                                // Failed to update username
                                                Toast.makeText(context, "Failed to update username: ${exception.message}", Toast.LENGTH_SHORT).show()
                                                onComplete(false)
                                            }
                                        AppUserData.currentUser?.username = newUsername
                                    }
                                }
                            }
                        }
                        builder.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                            onComplete(false)
                        }
                        builder.show()
                    } else {
                        // No user found with the current email
                        onComplete(false)
                    }
                }
                .addOnFailureListener { exception ->
                    // Error fetching user document
                    Toast.makeText(context, "Error fetching user document: ${exception.message}", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
        }
    }

    fun changePassword(context: Context, onComplete: (Boolean) -> Unit) {
        val currentUserEmail = AppUserData.currentUser?.email
        if (currentUserEmail != null) {
            // Show an AlertDialog to prompt the user for a new password
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Change Password")
            builder.setMessage("Enter a new password")

            val input = EditText(context)
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val newPassword = input.text.toString()
                if (newPassword.isBlank()) {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                } else if (newPassword.length < 8) {
                    isPasswordValid(context, newPassword)
                    onComplete(false)
                } else {
                    // Update the password in Firestore
                    usersCollection.whereEqualTo("email", currentUserEmail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val documentSnapshot = querySnapshot.documents[0]
                                documentSnapshot.reference.update("password", newPassword)
                                    .addOnSuccessListener {
                                        // Password updated successfully
                                        Toast.makeText(context, "New password: $newPassword", Toast.LENGTH_SHORT).show()
                                        onComplete(true)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Failed to update password
                                        Toast.makeText(context, "Failed to update password: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        onComplete(false)
                                    }
                                AppUserData.currentUser?.password = newPassword
                            } else {
                                // No user found with the current email
                                onComplete(false)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Error fetching user document
                            Toast.makeText(context, "Error fetching user document: ${exception.message}", Toast.LENGTH_SHORT).show()
                            onComplete(false)
                        }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                onComplete(false)
            }
            builder.show()
        }
    }

    private fun isPasswordValid(context: Context, password: String): Boolean {
        // Check if password length is at least 8 characters
        if (password.length < 8) {
            Toast.makeText(context, "Password must be 8 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        var hasLetter = false
        var hasNumber = false

        // Check each character in the password
        for (char in password) {
            // Check if character is a letter
            if (char.isLetter()) {
                hasLetter = true
            }
            // Check if character is a digit (number)
            if (char.isDigit()) {
                hasNumber = true
            }
        }
        if(!hasLetter){
            Toast.makeText(context, "Password must have at least one letter", Toast.LENGTH_SHORT).show()
        }
        if(!hasNumber) {
            Toast.makeText(context, "Password must have at least one number", Toast.LENGTH_SHORT).show()
        }
        // Check if the password has at least one letter and one number
        return hasLetter && hasNumber
    }

    fun changePhone(context: Context, onComplete: (Boolean) -> Unit) {
        val currentUserEmail = AppUserData.currentUser?.email
        if (currentUserEmail != null) {
            // Show an AlertDialog to prompt the user for a new phone number
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Change Phone")
            builder.setMessage("Enter a new Phone")

            val input = EditText(context)
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val newPhone = input.text.toString()
                if (newPhone.isBlank()) {
                    Toast.makeText(context, "Phone number cannot be empty", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                } else {
                    // Update the phone number in Firestore
                    usersCollection.whereEqualTo("email", currentUserEmail)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val documentSnapshot = querySnapshot.documents[0]
                                documentSnapshot.reference.update("phoneNumber", newPhone)
                                    .addOnSuccessListener {
                                        // Phone number updated successfully
                                        Toast.makeText(context, "New phone: $newPhone", Toast.LENGTH_SHORT).show()
                                        onComplete(true)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Failed to update phone number
                                        Toast.makeText(context, "Failed to update phone: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        onComplete(false)
                                    }
                                AppUserData.currentUser?.phoneNumber = newPhone
                            } else {
                                // No user found with the current email
                                onComplete(false)
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Error fetching user document
                            Toast.makeText(context, "Error fetching user document: ${exception.message}", Toast.LENGTH_SHORT).show()
                            onComplete(false)
                        }
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
                onComplete(false)
            }
            builder.show()
        }
    }

    private fun generateUserID(completion: (String) -> Unit) {
        val random = Random()
        val userID = StringBuilder()

        // Generate an 8-digit random number
        repeat(8) {
            userID.append(random.nextInt(10)) // Append a random digit (0-9)
        }
        val generatedUserID = userID.toString()

        // Check if the generated userID already exists in the database
        usersCollection.document(generatedUserID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // If the generated userID already exists, recursively call generateUniqueUserID to generate a new one
                    generateUserID(completion)
                } else {
                    // If the generated userID is unique, invoke the completion block with the userID
                    completion(generatedUserID)
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                Log.e(TAG, "Error checking if userID exists: $e")
            }
    }

    fun addFoodItemToPantry(foodItem: FoodItem) {
        // Access the current user's pantry collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val pantryRef = pantryCollection.document(userID)
                .collection("food")
            // Add the food item to the pantry collection
            pantryRef.add(foodItem)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Food item added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding food item", e)
                }
        }
    }

    fun removeFoodItemFromPantry(foodItem: FoodItem) {
        // Access the current user's pantry collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val pantryRef = pantryCollection.document(userID)
                .collection("food")
            // Remove the food item from the pantry collection
            pantryRef.whereEqualTo("name", foodItem.name)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document with the matching name
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Food item deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error deleting food item", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying food items", e)
                }
        }
    }

    fun fetchFoodItemsFromPantry(callback: (List<FoodItem>) -> Unit) {
        // Access the current user's pantry collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val pantryRef = pantryCollection.document(userID)
                .collection("food")
            // Fetch all food items from the pantry collection
            pantryRef.get()
                .addOnSuccessListener { result ->
                    val foodItems = mutableListOf<FoodItem>()
                    for (document in result) {
                        val foodItem = document.toObject(FoodItem::class.java)
                        foodItem?.let{
                            foodItems.add(it)
                        }
                    }
                    callback(foodItems)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching food items", e)
                    callback(emptyList()) // Pass an empty list in case of failure
                }
        }
    }

    fun addFriend(context: Context, username: String, onSuccess: () -> Unit) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            usersCollection.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener{ querySnapshot ->
                    if(!querySnapshot.isEmpty()) {
                        val friendRef = friendCollection.document(userID)
                            .collection("usernames")
                        // Add the friend to the pantry collection
                        val data = hashMapOf(
                            "username" to username
                        )
                        friendRef.add(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "Friend added with ID: ${documentReference.id}")
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding friend", e)
                            }
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("User not found")
                        builder.setMessage("The user with username $username does not exist.")
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
            }
        }
    }

    fun deleteFriend(username: String) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val friendRef = friendCollection.document(userID)
                .collection("usernames")
            // Remove the friend from the pantry collection
            friendRef.whereEqualTo("username", username)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document with the matching name
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Friend deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error deleting friend", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying food items", e)
                }
        }
    }

    fun getFriendsList(callback: (List<String>) -> Unit) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val friendRef = friendCollection.document(userID)
                .collection("usernames")
            friendRef.get()
                .addOnSuccessListener { result ->
                    val friendsList = mutableListOf<String>()
                    for (document in result) {
                        val username = document.getString("username") // Assuming the field name is "username"
                        username?.let {
                            friendsList.add(it)
                        }
                    }
                    callback(friendsList)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching friends list", e)
                    callback(emptyList()) // Pass an empty list in case of failure
                }
        }
    }

    fun changeRestrictions(restrictions: BooleanArray) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val restrictionRef = restrictionCollection.document(userID)
                .collection("restrictions").document("default")
            val restrictionsMap = mutableMapOf<String, Boolean>()
            for (i in restrictions.indices) {
                restrictionsMap["restriction$i"] = restrictions[i]
            }
            // Update the document with the new restrictions
            restrictionRef.set(restrictionsMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Restrictions updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating restrictions", e)
                }
        }
    }
    fun getRestrictions(callback: (BooleanArray) -> Unit) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val restrictionRef = restrictionCollection.document(userID)
                .collection("restrictions")
            restrictionRef.get()
                .addOnSuccessListener { result ->
                    val restrictions = BooleanArray(9) { false }
                    for (document in result) {
                        val data = document.data
                        for (i in 0 until 9) {
                            val restrictionKey = "restriction$i"
                            if (data.containsKey(restrictionKey)) {
                                restrictions[i] = data[restrictionKey] as? Boolean ?: false
                            }
                        }
                    }
                    callback(restrictions)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching restrictions", e)
                    // Pass an empty array in case of failure
                    callback(BooleanArray(9) { false })
                }
        }
    }

    fun changeAllergies(allergies: BooleanArray) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val allergiesRef = allergiesCollection.document(userID)
                .collection("allergies").document("default")
            val allergiesMap = mutableMapOf<String, Boolean>()
            for (i in allergies.indices) {
                allergiesMap["allergies$i"] = allergies[i]
            }
            // Update the document with the new restrictions
            allergiesRef.set(allergiesMap)
                .addOnSuccessListener {
                    Log.d(TAG, "Allergies updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating Allergies", e)
                }
        }
    }
    fun getAllergies(callback: (BooleanArray) -> Unit) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val allergiesRef = allergiesCollection.document(userID)
                .collection("allergies")
            allergiesRef.get()
                .addOnSuccessListener { result ->
                    val allergies = BooleanArray(10) { false }
                    for (document in result) {
                        val data = document.data
                        for (i in 0 until 10) {
                            val allergiesKey = "allergies$i"
                            if (data.containsKey(allergiesKey)) {
                                allergies[i] = data[allergiesKey] as? Boolean ?: false
                            }
                        }
                    }
                    callback(allergies)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching allergies", e)
                    // Pass an empty array in case of failure
                    callback(BooleanArray(9) { false })
                }
        }
    }

    fun addRecipe(recipe: Recipe) {
        // Access the current user's recipe collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("savedRecipes")
            val recipeMap = recipe.toMap()
            // Add the recipe to the recipe collection
            recipeRef.whereEqualTo("id", recipe.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty()) {
                        recipeRef.add(recipeMap)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "Recipe added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding recipe", e)
                            }
                    }else {
                        Log.d(TAG, "Recipe with ID ${recipe.id} already exists")
                    }
                }
        }
    }
    fun deleteRecipe(id: String) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("savedRecipes")
            // Remove the friend from the pantry collection
            recipeRef.whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document with the matching name
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Recipe deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error deleting recipe", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying recipe", e)
                }
        }
    }

    fun getRecipeList(currentUserID: String, callback: (MutableList<Recipe>) -> Unit) {
        currentUserID.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("savedRecipes")
            recipeRef.get()
                .addOnSuccessListener { result ->
                    val recipeList = mutableListOf<Recipe>()
                    for (document in result) {
                        val recipe = Recipe().apply {
                            this.id = document.getString("id")
                            this.title = document.getString("title")
                            this.imageUrl = document.getString("imageUrl")
                            this.instructions = document.getString("instructions")
                        }
                        recipeList.add(recipe)
                    }
                    callback(recipeList)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching recipe list", e)
                    callback(emptyList<Recipe>().toMutableList()) // Pass an empty list in case of failure
                }
        }
    }
    //
    fun addTodaysRecipe(recipe: Recipe) {
        // Access the current user's recipe collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("todaysRecipes")
            val recipeMap = recipe.toMap()
            // Add the recipe to the recipe collection
            recipeRef.whereEqualTo("id", recipe.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty()) {
                        recipeRef.add(recipeMap)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "Recipe added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding recipe", e)
                            }
                    }else {
                        Log.d(TAG, "Recipe with ID ${recipe.id} already exists")
                    }
                }
        }
    }
    fun deleteTodaysRecipe(id: String) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("todaysRecipes")
            // Remove the friend from the pantry collection
            recipeRef.whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document with the matching name
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Recipe deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error deleting recipe", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying recipe", e)
                }
        }
    }

    fun getTodaysRecipeList(currentUserID: String, callback: (MutableList<Recipe>) -> Unit) {
        currentUserID.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("todaysRecipes")
            recipeRef.get()
                .addOnSuccessListener { result ->
                    val recipeList = mutableListOf<Recipe>()
                    for (document in result) {
                        val recipe = Recipe().apply {
                            this.id = document.getString("id")
                            this.title = document.getString("title")
                            this.imageUrl = document.getString("imageUrl")
                            this.instructions = document.getString("instructions")
                        }
                        recipeList.add(recipe)
                    }
                    callback(recipeList)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching recipe list", e)
                    callback(emptyList<Recipe>().toMutableList()) // Pass an empty list in case of failure
                }
        }
    }

    fun getDatedRecipeList(currentUserID: String, callback: (MutableList<DatedRecipe>) -> Unit) {
        currentUserID.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("datedRecipes")
            recipeRef.get()
                .addOnSuccessListener { result ->
                    val recipeList = mutableListOf<DatedRecipe>()
                    for (document in result) {
                        val recipe = DatedRecipe().apply {
                            this.id = document.getString("id")
                            this.title = document.getString("title")
                            this.imageUrl = document.getString("imageUrl")
                            this.instructions = document.getString("instructions")
                            this.date = document.getString("date")
                        }
                        recipeList.add(recipe)
                    }
                    callback(recipeList)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching recipe list", e)
                    callback(emptyList<DatedRecipe>().toMutableList()) // Pass an empty list in case of failure
                }
        }
    }

    fun addDatedRecipe(datedrecipe: DatedRecipe) {
        // Access the current user's recipe collection
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("datedRecipes")
            val datedrecipeMap = datedrecipe.toMap()
            // Add the recipe to the recipe collection
            recipeRef.whereEqualTo("id", datedrecipe.id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty()) {
                        recipeRef.add(datedrecipeMap)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "Recipe added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding recipe", e)
                            }
                    }else {
                        Log.d(TAG, "Recipe with ID ${datedrecipe.id} already exists")
                    }
                }
        }
    }

    fun deleteDatedRecipe(id: String, date: String) {
        val currentUserID = AppUserData.currentUser?.userID
        currentUserID?.let { userID ->
            val recipeRef = recipeCollection.document(userID)
                .collection("datedRecipes")
            // Remove the friend from the pantry collection
            Log.d("What's going on...", "$id and $date")
            recipeRef.whereEqualTo("id", id)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document with the matching name
                        document.reference.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Recipe deleted")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error deleting recipe", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying recipe", e)
                }
        }
    }

    //

    fun getFriendRecipes(friendName: String, callback: (MutableList<Recipe>) -> Unit) {
        usersCollection.whereEqualTo("username", friendName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        getRecipeList(user.userID) { recipes ->
                            callback(recipes)
                        }
                    }
                }
            }
    }

    fun deleteAccount() {
        AppUserData.currentUser?.let {
            usersCollection.document(it.email)
                .delete()
                .addOnSuccessListener {
                    // Account deleted
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error deleting user document from Firestore", e)
                }
        }
    }

    fun logOut() {
        AppUserData.currentUser = null // Reset the current user in your app data
        SavedRecipes.recipes = mutableListOf<Recipe>()
        SavedRecipes.suggested_recipes = mutableListOf<Recipe>()
        db.clearPersistence() // Clear local Firestore cache
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                // Handle failure to clear Firestore cache or sign out
                Log.e(TAG, "Error logging out: ${e.message}", e)
            }
    }
}