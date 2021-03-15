package com.example.japanese_learning_app.News


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.japanese_learning_app.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_layout.view.*

class NewsAdapter (private val news : News, onNewsArticleListener: OnNewsArticleListener) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    companion object{
        public var newsArticlesList: List<News.Article> = ArrayList()
    }
    private lateinit var mOnNewsArticleListener: OnNewsArticleListener


    init {
        this.mOnNewsArticleListener = onNewsArticleListener
    }
    override fun getItemCount(): Int {
        return news.articles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_layout, parent, false),mOnNewsArticleListener )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        newsArticlesList = news.articles
        val articles = news.articles.get(position)
        holder.title.text = articles.title.toString()
        if(articles.urlToImage != null){
            Picasso.get().load(articles.urlToImage).into(holder.image)
        }else{
            holder.image.setImageResource(R.drawable.noimage)
        }

    }
    class ViewHolder(view : View, onNewsArticleListener : OnNewsArticleListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title = view.articleTitle
        val image = view.articleImage
        private lateinit var onNewsArticleListener : OnNewsArticleListener
        init {

            view.setOnClickListener(this)
            this.onNewsArticleListener = onNewsArticleListener
        }
        override fun onClick(v: View?) {
            onNewsArticleListener.onNewsArticleClick(adapterPosition)
        }


    }
    public interface OnNewsArticleListener{
        fun onNewsArticleClick(position:Int)
    }

}