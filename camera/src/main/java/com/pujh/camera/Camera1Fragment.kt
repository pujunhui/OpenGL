package com.pujh.camera

import android.R.attr
import android.content.res.Configuration
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.fragment.app.Fragment
import com.pujh.camera.databinding.FragmentCamera1Binding
import java.text.SimpleDateFormat
import java.util.Locale

class Camera1Fragment : Fragment() {
    private lateinit var binding: FragmentCamera1Binding
    private var facing = CameraInfo.CAMERA_FACING_BACK
    private var camera: Camera? = null
    private var cameraId: Int = 0

    private lateinit var orientationEventListener: OrientationEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orientationEventListener = object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) return
                val cameraInfo = CameraInfo()
                Camera.getCameraInfo(cameraId, cameraInfo)
                val orientation = (orientation + 45) / 90 * 90
                val rotation = if (cameraInfo.facing === CameraInfo.CAMERA_FACING_FRONT) {
                    (cameraInfo.orientation - attr.orientation + 360) % 360
                } else {  // back-facing camera
                    (cameraInfo.orientation + attr.orientation) % 360
                }
//                mParameters.setRotation(rotation)
//                Log.d(TAG, "phone orientation=$orientation")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamera1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.surfaceView.holder.addCallback(callback)

        binding.imageCaptureBtn.setOnClickListener {
            takePhoto()
        }
        binding.switchCameraBtn.setOnClickListener {
            switchCamera()
        }
        orientationEventListener.enable()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        orientationEventListener.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCamera()
    }

    private val callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceCreated")
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.d(TAG, "surfaceChanged width=$width, height=$height")
            stopCamera()
            startCamera(width, height)
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceDestroyed")
        }
    }

    private fun startCamera(width: Int, height: Int) {
        val (cameraId, cameraInfo) = (0 until Camera.getNumberOfCameras()).map { cameraId ->
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            Log.d(TAG, "camera$cameraId facing=${cameraInfo.facing} orientation=${cameraInfo.orientation}")
            cameraId to cameraInfo
        }.firstOrNull { (cameraId, cameraInfo) ->
//            cameraInfo.facing == facing
            cameraId == 1
        } ?: let {
            Toast.makeText(
                requireContext(),
                "Not find camera facing = $facing",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val camera = Camera.open(cameraId)
        val previewSizes = camera.parameters.supportedPreviewSizes

        val parameters = camera.parameters
        val cameraRotation = calcCameraRotation(cameraInfo)
        parameters.setRotation(cameraRotation)
        camera.parameters = parameters

        val displayRotation = calcDisplayRotation(cameraInfo)
        camera.setDisplayOrientation(displayRotation)
        camera.setPreviewDisplay(binding.surfaceView.holder)
        camera.startPreview()
        this.camera = camera
    }

    private fun calcDisplayRotation(cameraInfo: CameraInfo): Int {
        val orientation = cameraInfo.orientation
        val rotation = requireActivity().getDisplayRotation()
        var result: Int
        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (orientation + rotation) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else { // back-facing
            result = (orientation - rotation + 360) % 360
        }
        return result
    }

    private fun calcCameraRotation(cameraInfo: CameraInfo): Int {
        val orientation = cameraInfo.orientation
        val rotation = requireActivity().getDisplayRotation()
        Log.d(TAG, "camera orientation=$orientation, display rotation=$rotation")
        return if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            (orientation + rotation) % 360
        } else { // back-facing camera
            val landscapeFlip = if (isLandscape(rotation)) 180 else 0
            (orientation + rotation + landscapeFlip) % 360
        }
    }


    private fun isLandscape(orientationDegrees: Int): Boolean {
        return (orientationDegrees == Surface.ROTATION_90 ||
                orientationDegrees == Surface.ROTATION_270)
    }

    private fun stopCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private fun takePhoto() {
        camera?.takePicture(null, null) { data, camera ->
            val image: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val name = SimpleDateFormat(FILE_NAME_FORMAT, Locale.CHINA)
                .format(System.currentTimeMillis())
            val contentValues = contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to name,
                MediaStore.MediaColumns.MIME_TYPE to "image/jpeg",
            )
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
            val contentResolver = requireContext().contentResolver
            val imageUri = contentResolver.insert(image, contentValues) ?: return@takePicture
            try {
                contentResolver.openOutputStream(imageUri)?.use {
                    it.write(data)
                    it.flush()
                } ?: return@takePicture
                Toast.makeText(requireContext(), "Take picture success!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                contentResolver.delete(imageUri, null, null)
            }
            camera.startPreview()
        }
    }

    private fun switchCamera() {
        stopCamera()
        facing = if (facing == CameraInfo.CAMERA_FACING_BACK) {
            CameraInfo.CAMERA_FACING_FRONT
        } else {
            CameraInfo.CAMERA_FACING_BACK
        }
        startCamera(0, 0)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        Log.d(TAG, "orientation = $orientation")
    }

    companion object {
        private const val TAG = "Camera1Fragment"
        private const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}