package com.example.japanese_learning_app.News


import com.google.gson.annotations.SerializedName

data class News(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
) {
    data class Article(
        val source: Source,
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: Any
    ) {
        data class Source(
            val id: Any,
            val name: String
        )
    }
}