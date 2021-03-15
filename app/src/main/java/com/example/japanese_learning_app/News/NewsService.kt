package com.example.japanese_learning_app.News

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
        @GET("top-headlines?country=jp&apiKey=397b0afebc5a457286040fc951de4538")

        fun getNews(@Query("category") category:String): Call<News>
    }