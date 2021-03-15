package com.example.japanese_learning_app.Dictionary

import android.content.Context
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.japanese_learning_app.R
import com.example.japanese_learning_app.Utils.BottomNavViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_dictionary.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class DictionaryActivity : AppCompatActivity(), DictionaryAdapter.OnDictionaryListener, SensorEventListener {
    private val TAG = "DictionaryActivity"
    private val mContext : Context = this@DictionaryActivity
    companion object{
        private val ACTIVITY_NUM : Int = 2
    }
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    lateinit var prefs : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private var lastSearchedWord =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)
        sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor  = prefs.edit()
        setupBottomNavView()
        if(savedInstanceState != null){
            val word = savedInstanceState.getString("search")
            searchWord(word)
        }
            btnSearch.setOnClickListener{searchWord()}
        val vocabularyListWord = intent.getStringExtra("EXTRA_WORD")
        if(vocabularyListWord != null){
            txtSearch.setText(vocabularyListWord)
            loadData(vocabularyListWord)
            txtSearch.text.clear()
        }
    }

    override fun onResume() {
        super.onResume()
        light?.let { light ->
            sensorManager.registerListener(this, light,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search", lastSearchedWord)
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

    private fun searchWord(searchWord : String? = null){
        var word = searchWord
        if(word == null){
            word = txtSearch.text.toString()
        }
        loadData(word)
        lastSearchedWord = word
        txtSearch.text.clear()
    }

    private fun setupBottomNavView(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        BottomNavViewHelper.enableNavigation(mContext, navView)
        val menu : Menu = navView.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun loadData(word:String) {
        val service  = ServiceBuilder.buildService(DictionaryService::class.java)
        val requestCall = service.getWord(word)

        requestCall.enqueue(object : Callback<Dictionary> {
            override fun onResponse(call: Call<Dictionary>,
                                    response: Response<Dictionary>
            ) {
                if (response.isSuccessful){
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        dictionaryRecyclerView.layoutManager = GridLayoutManager(mContext, 2)
                    }else{
                        dictionaryRecyclerView.layoutManager = LinearLayoutManager(mContext)
                    }
                    dictionaryRecyclerView.adapter = DictionaryAdapter(response.body()!!,this@DictionaryActivity)
                }else{
                    AlertDialog.Builder(mContext)
                        .setTitle("API error")
                        .setMessage("Response, but something went wrong. Please try again later.")
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
            override fun onFailure(call: Call<Dictionary>, t: Throwable) {
                AlertDialog.Builder(mContext)
                    .setTitle("API error")
                    .setMessage("No response, and something went wrong. Please check your internet connection and try again.")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        })
    }

    override fun onDictionaryClick(position: Int) {
        var wordToAdd = DictionaryAdapter.wordList[position].japanese[0].word
        if(wordToAdd == null){
            wordToAdd = DictionaryAdapter.wordList[position].japanese[0].reading.toString()
        }
        val wordList = getWords()
        val toast : Toast
        if(!wordList.contains(wordToAdd)){
            wordList.add(wordToAdd)
            saveWords(wordList)
             toast =Toast.makeText(this, "The word $wordToAdd has been added", Toast.LENGTH_SHORT)
        }else{
            toast = Toast.makeText(this, "The word $wordToAdd is already added", Toast.LENGTH_SHORT)
        }
        toast.show()
    }

    private fun getWords() : ArrayList<String>{
        val gson: Gson = Gson()
        var json : String? = prefs.getString("words",null)
        val type : Type =  object: TypeToken<ArrayList<String>>(){}.type
        return gson.fromJson<ArrayList<String>>(json, type)
    }

    private fun saveWords(words : ArrayList<String>){
        val gson: Gson = Gson()
        val json = gson.toJson(words)
        editor.putString("words", json)
        editor.apply()
    }

}