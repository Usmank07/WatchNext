package com.example.watchnext

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// ---------- DATA CLASSES ----------

// One movie / show item – matches elements inside the JSON array
data class ImdbTitle(
    val id: String,
    val primaryTitle: String?,
    val originalTitle: String?,
    val type: String?,
    val description: String?,
    val primaryImage: String?,
    val startYear: Int?,
    val averageRating: Double?,
    val numVotes: Int?
)

// ---------- API CONFIG ----------

object ApiConfig {
    const val RAPID_API_KEY =
        "ebb95589f2msh2cbbe8cbe037d57p1a459ajsnd9bb03a75c47"  // <- your key
    const val RAPID_API_HOST = "imdb236.p.rapidapi.com"
    const val BASE_URL = "https://imdb236.p.rapidapi.com/"
}

// ---------- RETROFIT SERVICE ----------

interface MovieApiService {

    // GET https://imdb236.p.rapidapi.com/api/imdb/cast/nm0000190/titles
    // The root response is a JSON ARRAY, so we return List<ImdbTitle>
    @GET("api/imdb/cast/{personId}/titles")
    suspend fun getCastTitles(
        @Path("personId") personId: String,
        @Header("x-rapidapi-key") apiKey: String = ApiConfig.RAPID_API_KEY,
        @Header("x-rapidapi-host") apiHost: String = ApiConfig.RAPID_API_HOST
    ): List<ImdbTitle>
}

object MovieApi {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val service: MovieApiService = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApiService::class.java)
}