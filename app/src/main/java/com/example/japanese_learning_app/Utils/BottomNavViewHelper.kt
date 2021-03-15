package com.example.japanese_learning_app.Utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.example.japanese_learning_app.*
import com.example.japanese_learning_app.Camera.CameraActivity
import com.example.japanese_learning_app.Dictionary.DictionaryActivity
import com.example.japanese_learning_app.News.MainActivity
import com.example.japanese_learning_app.VocabularyList.VocabularyListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavViewHelper {
    private val TAG = "BottomNavViewHelper"
    companion object{
        public fun setupBottomNavView(navView : BottomNavigationView){

        }

        public fun enableNavigation(context:Context, navView: BottomNavigationView){
            navView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener  { item ->
                when (item.itemId) {
                    R.id.navigation_news -> {

                        val intent1 : Intent = Intent(context,
                            MainActivity::class.java)
                        context.startActivity(intent1)
                    }
                    R.id.navigation_vocabulary -> {
                        val intent2 : Intent = Intent(context,
                            VocabularyListActivity::class.java)
                        context.startActivity(intent2)
                    }
                    R.id.navigation_dictionary -> {
                        val intent3 : Intent =Intent(context,
                            DictionaryActivity::class.java)
                            context.startActivity(intent3)
                    }
                    R.id.navigation_camera -> {
                        val intent4 : Intent = Intent(context,
                            CameraActivity::class.java)
                        context.startActivity(intent4)
                    }
                }
                false
            })
        }

    }
}