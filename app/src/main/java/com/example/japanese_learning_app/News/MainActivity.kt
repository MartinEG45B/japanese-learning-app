package com.example.japanese_learning_app.News

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.japanese_learning_app.R
import com.example.japanese_learning_app.Utils.BottomNavViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), NewsAdapter.OnNewsArticleListener, SensorEventListener {
    private val TAG = "MainActivity"
    private val mContext : Context = this@MainActivity
    companion object{
        private val ACTIVITY_NUM : Int = 0
    }
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)


        this.title = "ニュース / News"
        setupBottomNavView()
        setupDropdownMenu()
        if(savedInstanceState!= null){
            val category = savedInstanceState.getString("category")
            loadData(category as String)
        }else{
            loadData("general")
        }
        list_category_tv.setOnItemClickListener{parent, view, position, id ->
            changeCategory(parent, position)
        }
    }

    override fun onResume() {
        super.onResume()
        setupDropdownMenu()
        light?.let { light ->
            sensorManager.registerListener(this, light,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("category", list_category_tv.text.toString())
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

    private fun changeCategory(parent : AdapterView<*>, position : Int){
        loadData(parent.getItemAtPosition(position).toString())
    }
    private fun setupBottomNavView(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        BottomNavViewHelper.enableNavigation(mContext, navView)
        val menu :Menu = navView.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    private fun setupDropdownMenu(){
        val items = listOf("General", "Business", "Entertainment", "Health","Science","Sports","Technology")
        val adapter = ArrayAdapter(mContext, R.layout.support_simple_spinner_dropdown_item, items)
        list_category_tv.setAdapter(adapter)
    }


    private fun loadData(category:String) {
        val service  = ServiceBuilder.buildService(NewsService::class.java)
        val requestCall = service.getNews(category)

        requestCall.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>,
                                    response: Response<News>
            ) {
                if (response.isSuccessful){
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        newsRecyclerView.layoutManager = GridLayoutManager(mContext,2)
                    }else{
                        newsRecyclerView.layoutManager = LinearLayoutManager(mContext)
                    }
                    newsRecyclerView.adapter = NewsAdapter(response.body()!!,this@MainActivity)
                }else{
                    AlertDialog.Builder(mContext)
                        .setTitle("API error")
                        .setMessage("Response, but something went wrong. Please try again later.")
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }
            override fun onFailure(call: Call<News>, t: Throwable) {
                AlertDialog.Builder(mContext)
                    .setTitle("API error")
                    .setMessage("No response, and something went wrong. Please check your internet connection and try again.")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        })
    }

    override fun onNewsArticleClick(position: Int)
    {
        val url = NewsAdapter.newsArticlesList[position].url.toString()
        openWebsite(url)
    }

    private fun openWebsite(url : String) {
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }


}