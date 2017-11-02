package com.mrussek.steptracker

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.mrussek.steptracker.render.Shader
import com.mrussek.steptracker.render.Texture
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val tag = javaClass.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)

        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT

        val surface = Surface(this)
        setContentView(surface)
        Shader.addSource("quadVert", loadShader(resources, R.raw.quadvert));
        Shader.addSource("quadFrag", loadShader(resources, R.raw.quadfrag));
        Texture.loadFile(applicationContext, R.drawable.westeros_smallest, "map");
        Texture.loadFile(applicationContext, R.drawable.westeros_line, "line");

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter != null) {
            Log.d(tag, stepCounter.name)

            sensorManager.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    Log.d(javaClass.canonicalName, "Accuracy changed")
                }

                override fun onSensorChanged(sensorEvent: SensorEvent) {
                    Log.d(javaClass.canonicalName, sensorEvent.values[0].toString())
                    surface.renderer.setSteps(sensorEvent.values[0].toInt())
                }
            }, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d(tag, "No step sensor!!!!!")
        }
    }

    @Throws(IOException::class)
    private fun loadShader(res: Resources, resHandle: Int): String {
        val inputStream = res.openRawResource(resHandle)

        val inputreader = InputStreamReader(inputStream)
        val buffreader = BufferedReader(inputreader)
        var line: String?
        val text = StringBuilder()

        line = buffreader.readLine()
        while (line != null) {
            text.append(line)
            text.append('\n')
            line = buffreader.readLine()
        }

        return text.toString()
    }
}
