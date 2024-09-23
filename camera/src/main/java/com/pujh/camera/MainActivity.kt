package com.pujh.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pujh.camera.databinding.ActivityMainBinding
import com.pujh.ffmpeg.ExternalStoragePermission
import com.pujh.ffmpeg.checkExternalStoragePermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var cameraType = -1

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            setCameraType(cameraType)
        } else {
            Toast.makeText(
                this,
                "Permissions not granted by the user!",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    private val requestPermissionLauncher1 = registerForActivityResult(
        ExternalStoragePermission()
    ) { granted ->
        if (granted) {
            binding.cameraType.check(R.id.camera1_btn)
        } else {
            Toast.makeText(
                this,
                "Permissions not granted by the user!",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.cameraType.setOnCheckedChangeListener { _, checkedId ->
            val cameraType = when (checkedId) {
                R.id.camera1_btn -> CAMERA_TYPE_CAMERA1
                R.id.camera2_btn -> CAMERA_TYPE_CAMERA2
                R.id.camerax_btn -> CAMERA_TYPE_CAMERAX
                else -> throw IllegalStateException("error camera type!")
            }
            setCameraType(cameraType)
        }

        if (checkExternalStoragePermission()) {
            binding.cameraType.check(R.id.camera1_btn)
        } else {
            requestPermissionLauncher1.launch(null)
        }
        binding.cameraType.check(R.id.camera1_btn)
    }

    private fun setCameraType(cameraType: Int) {
        this.cameraType = cameraType
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        val fragment = when (cameraType) {
            CAMERA_TYPE_CAMERA1 -> Camera1Fragment()
            CAMERA_TYPE_CAMERA2 -> Camera2Fragment()
            CAMERA_TYPE_CAMERAX -> CameraXFragment()
            else -> throw IllegalStateException("error camera type!")
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private const val TAG = "CameraDemo"

        private const val CAMERA_TYPE_CAMERA1 = 1
        private const val CAMERA_TYPE_CAMERA2 = 2
        private const val CAMERA_TYPE_CAMERAX = 3
    }
}