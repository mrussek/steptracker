package com.mrussek.steptracker.render

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import rx.Emitter
import rx.Observable
import java.util.concurrent.TimeUnit

interface StepCounter {
    val steps: Observable<Int>
}

class ConstantIntervalStepCounter : StepCounter {
    override val steps: Observable<Int> = Observable.interval(1, TimeUnit.SECONDS).map { it.toInt() }
}

class FirebaseSyncedStepCounter(stepCounter: StepCounter, private val stepDatabaseReference: DatabaseReference) : StepCounter {
    private val subscription = stepCounter.steps.subscribe {
        stepDatabaseReference.setValue(it)
    }

    override val steps: Observable<Int> = Observable.create({ async ->
        val listener = FirebaseStepValueEventListener {
            async.onNext(it)
        }

        async.setCancellation {
            subscription.unsubscribe()
            stepDatabaseReference.removeEventListener(listener)
        }

        stepDatabaseReference.addValueEventListener(listener)

    }, Emitter.BackpressureMode.NONE)

    private class FirebaseStepValueEventListener(private val listener: (Int) -> Unit) : ValueEventListener {
        override fun onCancelled(databaseError: DatabaseError) {
            // Nothing yet
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            listener(dataSnapshot.getValue(Int::class.java)!!)
        }
    }
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