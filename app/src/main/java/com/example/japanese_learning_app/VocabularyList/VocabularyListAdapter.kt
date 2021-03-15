package com.example.japanese_learning_app.VocabularyList


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.japanese_learning_app.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_vocabulary_list.*
import kotlinx.android.synthetic.main.vocabulary_list_layout.view.*
import java.lang.reflect.Type

class VocabularyListAdapter (private val savedWords : ArrayList<String>, onVocabularyListListener: OnVocabularyListListener, prefs : SharedPreferences, recyclerView : RecyclerView, context: Context, orientation : Int) :
    RecyclerView.Adapter<VocabularyListAdapter.ViewHolder>() {
    private var savedWordsToAdd : ArrayList<String> = ArrayList()
    private lateinit var mOnVocabularyListListener: OnVocabularyListListener
    private lateinit var prefs : SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private  lateinit var context: Context
    private var orientation = 0

    init {
        this.mOnVocabularyListListener = onVocabularyListListener
        this.savedWordsToAdd = savedWords
        this.prefs = prefs
        this.recyclerView = recyclerView
        this.context = context
        this.orientation = orientation
    }
    override fun getItemCount(): Int {
        return savedWordsToAdd.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.vocabulary_list_layout, parent, false),mOnVocabularyListListener )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.word.text = savedWordsToAdd.get(position)
        holder.btnDelete.setOnClickListener { deleteWord(position) }
    }

    class ViewHolder(view : View, mOnVocabularyListListener : OnVocabularyListListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val word = view.vocabularyListWord
        val btnDelete = view.btnDelete

        private lateinit var mOnVocabularyListListener : OnVocabularyListListener
        init {
            view.setOnClickListener(this)
            this.mOnVocabularyListListener = mOnVocabularyListListener
        }
        override fun onClick(v: View?) {
            mOnVocabularyListListener.onVocabularyListClick(adapterPosition)
        }

    }
    public interface OnVocabularyListListener{
        fun onVocabularyListClick(position:Int)
    }

    private fun deleteWord(position: Int){
        val prefs : SharedPreferences = this.prefs
        val editor : SharedPreferences.Editor = prefs.edit()
        val gson: Gson = Gson()
        var json : String? = prefs.getString("words",null)
        val type : Type =  object: TypeToken<ArrayList<String>>(){}.type
        val wordList = gson.fromJson<ArrayList<String>>(json, type)
        wordList.removeAt(position)
        json = gson.toJson(wordList)
        editor.putString("words", json)
        editor.apply()
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.layoutManager = GridLayoutManager(context,2)
        }else{
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
        recyclerView.adapter = VocabularyListAdapter(wordList,mOnVocabularyListListener, prefs, recyclerView, context, orientation)
    }

}
