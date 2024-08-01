package com.pujh.camera

import android.Manifest
import android.annotation.SuppressLint
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.CAMERA_SERVICE
import androidx.fragment.app.Fragment
import com.pujh.camera.databinding.FragmentCamera2Binding


class Camera2Fragment : Fragment() {
    private lateinit var binding: FragmentCamera2Binding

    private var hasCameraPermission = false
    private var surface: Surface? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasCameraPermission = true
            startCamera()
        } else {
            Toast.makeText(
                this.requireContext(),
                "Permissions not granted by the user!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamera2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.surfaceView.holder.addCallback(callback)

        binding.imageCaptureBtn.setOnClickListener {
//            takePhoto()
        }
        binding.switchCameraBtn.setOnClickListener {
//            switchCamera()
        }
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        startCamera()
    }

    private val callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            surface = holder.surface
            startCamera()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            surface = null
            stopCamera()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        val surface = surface
        val hasCameraPermission = hasCameraPermission
        if (surface == null || !hasCameraPermission) {
            return
        }
        val manager = requireContext().getSystemService(CAMERA_SERVICE) as CameraManager
        manager.cameraIdList.forEach { cameraId ->
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val facing = characteristics[CameraCharacteristics.LENS_FACING]!!
            when (facing) {
                CameraCharacteristics.LENS_FACING_BACK -> {
                    Log.d(TAG, "Camera$cameraId facing is back")
                }

                CameraCharacteristics.LENS_FACING_FRONT -> {
                    Log.d(TAG, "Camera$cameraId facing is front")
                }

                CameraCharacteristics.LENS_FACING_EXTERNAL -> {
                    Log.d(TAG, "Camera$cameraId facing is external")
                }
            }
        }
        val cameraId = "0"
        val characteristics = manager.getCameraCharacteristics(cameraId)

        //获取摄像头朝向
        val facing = characteristics[CameraCharacteristics.LENS_FACING]!!
        //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
        val map = characteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]!!
        //获取传感器方向
        val orientation = characteristics[CameraCharacteristics.SENSOR_ORIENTATION]!!

        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                val outputs = listOf<Surface>()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    val config = SessionConfiguration()
//                    camera.createCaptureSession(config)
                } else {
                    camera.createCaptureSession(
                        outputs,
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {

                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {

                            }
                        }, null
                    )
                }

            }

            override fun onClosed(camera: CameraDevice) {
                Log.d(TAG, "Camera closed!")
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "Camera onDisconnected!")
                stopCamera()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "Camera onError: $error")
                stopCamera()
            }

        }, null)
    }

    private fun stopCamera() {

    }

    companion object {
        private const val TAG = "Camera2Fragment"
    }
}