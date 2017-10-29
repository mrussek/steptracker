package com.mrussek.steptracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val tag = javaClass.canonicalName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                }
            }, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d(tag, "No step sensor!!!!!")
        }
    }
}
