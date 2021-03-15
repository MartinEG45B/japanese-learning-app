package com.example.japanese_learning_app.VocabularyList

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.japanese_learning_app.Dictionary.DictionaryActivity
import com.example.japanese_learning_app.R
import com.example.japanese_learning_app.Utils.BottomNavViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_vocabulary_list.*
import java.lang.reflect.Type



class VocabularyListActivity : AppCompatActivity(), VocabularyListAdapter.OnVocabularyListListener,
    SensorEventListener {
    private val TAG = "VocabularyListActivity"
    private val mContext : Context = this@VocabularyListActivity
    companion object{
        private val ACTIVITY_NUM : Int = 1
    }
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_list)

        Log.d(TAG, "onCreate: started")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        btnAdd.setOnClickListener{addWord()}
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = prefs.edit()
        setupBottomNavView()
        showVocabularyList()

    }

    override fun onResume() {
        super.onResume()
        light?.let { light ->
            sensorManager.registerListener(this, light,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: Accuracy change")
    }

    var oldLux: Float = 0F
    override fun onSensorChanged(event: SensorEvent) {
        val lux = event.values[0]
        if (lux != oldLux) {
            if(lux >= 300){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            oldLux = lux
        }
    }

    private fun addWord(){
        var wordList = getWords()
        if(wordList == null){
            wordList = ArrayList<String>()
        }
        val word = txtAddWord.text.toString()
        if(wordList.contains(word)){
            Toast.makeText(this, "The word $word is already added", Toast.LENGTH_SHORT).show()
        }else if(word == ""){
            Toast.makeText(this, "Nothing typed", Toast.LENGTH_SHORT).show()
        }else{
            wordList.add(word)
            saveWords(wordList)
            showVocabularyList()
        }
        txtAddWord.text.clear()
    }

    private fun getWords() : ArrayList<String>{
        val gson:Gson = Gson()
        var json : String? = prefs.getString("words",null)
        val type : Type =  object: TypeToken<ArrayList<String>>(){}.type
        return gson.fromJson<ArrayList<String>>(json, type)
    }

    private fun saveWords(words : ArrayList<String>){
        val gson:Gson = Gson()
        val json = gson.toJson(words)
        editor.putString("words", json)
        editor.apply()
    }

    private fun showVocabularyList(){
        val gson:Gson = Gson()
        val json : String? = prefs.getString("words",null)
        val type : Type =  object: TypeToken<ArrayList<String>>(){}.type
        val wordList = gson.fromJson<ArrayList<String>>(json, type)
        if(wordList == null){
            initialisePrefs()
        }else{
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                vocabularyRecyclerView.layoutManager = GridLayoutManager(mContext,2)
            }else{
                vocabularyRecyclerView.layoutManager = LinearLayoutManager(mContext)
            }
            vocabularyRecyclerView.adapter = VocabularyListAdapter(wordList,this@VocabularyListActivity, prefs, vocabularyRecyclerView, mContext,getResources().getConfiguration().orientation)
        }
    }

    private fun initialisePrefs(){
        val gson:Gson = Gson()
        val json : String = gson.toJson(ArrayList<String>())
        editor.putString("words", json)
        editor.apply()
    }
    private fun setupBottomNavView(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        BottomNavViewHelper.enableNavigation(mContext, navView)
        val menu : Menu = navView.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    override fun onVocabularyListClick(position: Int) {
        val wordList = getWords()
        val word = wordList[position]
        val dictionaryIntent : Intent = Intent(this,DictionaryActivity::class.java).apply {
            putExtra("EXTRA_WORD", word)
        }
        startActivity(dictionaryIntent)
    }
}
