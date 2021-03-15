package com.example.japanese_learning_app.Dictionary


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.japanese_learning_app.R
import kotlinx.android.synthetic.main.dictionary_layout.view.*


class DictionaryAdapter (private val words : Dictionary, OnDictionaryListener: OnDictionaryListener) :
    RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {
    companion object{
        public var wordList: List<Dictionary.Data> = ArrayList()
    }
    private lateinit var mOnDictionaryListener: OnDictionaryListener


    init {
        this.mOnDictionaryListener = OnDictionaryListener
    }
    override fun getItemCount(): Int {
        return words.data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dictionary_layout, parent, false),mOnDictionaryListener )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        wordList = words.data
        val word = words.data.get(position)
        val jpWord = word.japanese.elementAt(0).word
        if(jpWord != null){
            holder.word.text = word.japanese.elementAt(0).word
            holder.reading.text = word.japanese.elementAt(0).reading
        }else{
            holder.word.text = word.japanese.elementAt(0).reading
        }
        val partOfSpeech = word.senses.elementAt(0).partsOfSpeech.joinToString()
        if(partOfSpeech != ""){
            holder.part_of_speech.text =partOfSpeech
        }
        holder.meaning.text = word.senses.elementAt(0).englishDefinitions.joinToString()

    }


    class ViewHolder(view : View, OnDictionaryListener : OnDictionaryListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val word = view.dictionaryWord
        val reading = view.dictionaryReading
        val part_of_speech = view.dictionaryPartOfSpeech
        val meaning = view.dictionaryMeaning
        private lateinit var OnDictionaryListener : OnDictionaryListener
        init {
            view.setOnClickListener(this)
            this.OnDictionaryListener = OnDictionaryListener
        }
        override fun onClick(v: View?) {
            OnDictionaryListener.onDictionaryClick(adapterPosition)
        }


    }
    public interface OnDictionaryListener{
        fun onDictionaryClick(position:Int)
    }

}