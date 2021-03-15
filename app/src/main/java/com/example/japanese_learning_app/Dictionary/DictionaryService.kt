package com.example.japanese_learning_app.Dictionary

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryService {
    @GET("words?")
    fun getWord(@Query("keyword") keyword:String): Call<Dictionary>
}