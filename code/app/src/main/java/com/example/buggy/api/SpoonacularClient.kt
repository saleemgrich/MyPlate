package com.example.buggy.api

import android.content.ContentValues.TAG
import android.util.Log
import com.example.buggy.data.model.Recipe
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory


class SpoonacularClient(private val apiKey: String) {
    private val BASE_URL = "https://api.spoonacular.com/"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .build()
            val requestBuilder = original.newBuilder()
                .url(url)
            val request = requestBuilder.build()
            chain.proceed(request)
        })
        .addInterceptor(ApiKeyInterceptor(apiKey)) // Custom interceptor for adding API key
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: SpoonacularApi = retrofit.create(SpoonacularApi::class.java)

    suspend fun searchRecipes(query: String, number: Int, cuisine: String, diet: String,
                              allergies: String, ingredients: String, maxReadyTime: Int): List<Recipe>? {
        return try {
            val response = apiService.getRecipes(query, number, cuisine, diet, allergies, ingredients,
                true, true, maxReadyTime)?.awaitResponse()
            if (response?.isSuccessful == true) {
                Log.d(TAG, response.raw().toString())
                response.body()?.results?.forEach { recipe ->
                    println("Recipe: ${recipe.title}")
                    var recipeInstructions = ""
                    recipe.analyzedInstructions?.forEach { instruction ->
                        instruction.steps?.forEach { step ->
                            println("Step ${step.number}: ${step.step}")
                            recipeInstructions += "Step ${step.number}: ${step.step}\n"
                        }
                    }
                    recipe.instructions = recipeInstructions
                }
                response.body()?.results
            } else {
                Log.e(TAG, "Unsuccessful response")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// Custom interceptor to add API key to requests
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalHttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("apiKey", apiKey)
            .build()

        val requestBuilder = original.newBuilder()
            .url(url)

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}