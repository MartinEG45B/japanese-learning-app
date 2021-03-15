package com.example.japanese_learning_app.Camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.japanese_learning_app.R
import com.example.japanese_learning_app.Utils.BottomNavViewHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "CameraActivity"
    private val mContext : Context = this@CameraActivity
    companion object{
        private val ACTIVITY_NUM : Int = 3
    }
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val REQUEST_CODE_PERMISSIONS = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager

        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        setupBottomNavView()
        btnTakePhoto.setOnClickListener{ takePhoto() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
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
    private fun setupBottomNavView(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        BottomNavViewHelper.enableNavigation(mContext, navView)
        val menu : Menu = navView.menu
        val menuItem : MenuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.setChecked(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray ) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(this, "Permissions not granted by the user", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT,Locale.UK).format(System.currentTimeMillis()) + ".jpg")

        val outputOpts = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOpts,
            ContextCompat.getMainExecutor(this), object :
                ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Toast.makeText(this@CameraActivity,"Photo saved", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.createSurfaceProvider())}

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,
                    imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}