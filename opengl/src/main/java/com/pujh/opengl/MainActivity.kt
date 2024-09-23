package com.pujh.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pujh.opengl.basic.BasicFragment
import com.pujh.opengl.camera.CameraXFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, BasicFragment())
            .commit()
    }
}