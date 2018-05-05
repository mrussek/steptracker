package com.mrussek.steptracker

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.mrussek.steptracker.render.ConstantIntervalStepCounter
import com.mrussek.steptracker.render.FirebaseSyncedStepCounter
import com.mrussek.steptracker.render.Shader
import com.mrussek.steptracker.render.Texture
import rx.Subscription
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val tag = javaClass.canonicalName

    private lateinit var stepSubscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surface = Surface(this)
        setContentView(surface)
        Shader.addSource("quadVert", loadShader(resources, R.raw.quadvert))
        Shader.addSource("quadFrag", loadShader(resources, R.raw.quadfrag))
        Texture.loadFile(applicationContext, R.drawable.westeros_smallest, "map")
        Texture.loadFile(applicationContext, R.drawable.westeros_line, "line")

        val stepCounter = ConstantIntervalStepCounter() // Change to `SensorStepCounter` for real data
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reference = firebaseDatabase.getReference("steps")
        val firebaseStepCounter = FirebaseSyncedStepCounter(stepCounter, reference)

        stepSubscription = firebaseStepCounter.steps
                .doOnNext { Log.d(tag, "Steps taken: $it") }
                .subscribe { surface.renderer.setSteps(it) }
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

    override fun onDestroy() {
        super.onDestroy()

        stepSubscription.unsubscribe()
    }
}
