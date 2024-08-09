package com.example.mygame.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.mygame.RUN

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(RUN(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
