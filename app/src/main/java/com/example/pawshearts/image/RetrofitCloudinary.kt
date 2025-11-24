package com.example.pawshearts.image

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCloudinary {
    private const val BASE_URL = "https://api.cloudinary.com/"

    val instance: CloudinaryService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryService::class.java)
    }
}