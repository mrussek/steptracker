package com.mrussek.steptracker.render

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import rx.Emitter
import rx.Observable
import java.util.concurrent.TimeUnit

interface StepCounter {
    val steps: Observable<Int>
}

class ConstantIntervalStepCounter : StepCounter {
    override val steps: Observable<Int> = Observable.interval(1, TimeUnit.SECONDS).map { it.toInt() }
}

class SensorStepCounter(sensorManager: SensorManager) : StepCounter {
    override val steps: Observable<Int> = Observable.create({ async ->
        val listener = StepSensorEventListener {
            async.onNext(it)
        }

        async.setCancellation { sensorManager.unregisterListener(listener) }

        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)

    }, Emitter.BackpressureMode.NONE)

    private class StepSensorEventListener(val callback: (Int) -> Unit) : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Nothing
        }

        override fun onSensorChanged(sensorEvent: SensorEvent) {
            callback(sensorEvent.values[0].toInt())
        }
    }
}