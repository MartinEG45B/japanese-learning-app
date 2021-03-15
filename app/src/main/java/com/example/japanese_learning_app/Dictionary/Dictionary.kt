package com.example.japanese_learning_app.Dictionary


import com.google.gson.annotations.SerializedName

data class Dictionary(
    val meta: Meta,
    val `data`: List<Data>
) {
    data class Meta(
        val status: Int
    )

    data class Data(
        val slug: String,
        @SerializedName("is_common")
        val isCommon: Boolean,
        val tags: List<String>,
        val jlpt: List<String>,
        val japanese: List<Japanese>,
        val senses: List<Sense>,
        val attribution: Attribution
    ) {
        data class Japanese(
            val word: String,
            val reading: String
        )

        data class Sense(
            @SerializedName("english_definitions")
            val englishDefinitions: List<String>,
            @SerializedName("parts_of_speech")
            val partsOfSpeech: List<String>,
            val links: List<Link>,
            val tags: List<String>,
            val restrictions: List<Any>,
            @SerializedName("see_also")
            val seeAlso: List<String>,
            val antonyms: List<Any>,
            val source: List<Any>,
            val info: List<Any>,
            val sentences: List<Any>
        ) {
            data class Link(
                val text: String,
                val url: String
            )
        }

        data class Attribution(
            val jmdict: Boolean,
            val jmnedict: Boolean,
            val dbpedia: Any
        )
    }
}