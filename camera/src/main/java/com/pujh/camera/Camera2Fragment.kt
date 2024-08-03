package com.pujh.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.hardware.camera2.params.SessionConfiguration.SESSION_REGULAR
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.CAMERA_SERVICE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.pujh.camera.databinding.FragmentCamera2Binding

/**
 * Camera1录制视频可以使用 MediaRecorder.setCamera(Camera) 方法，
 * 将MediaRecorder创建，延迟到录制时，而无需重建Camera
 *
 * Camera2录制视频，需要在创建session时，就指定需要输出的surface。
 * 而MediaRecorder.getSurface(),必须要在调用prepare()后，而prepare()调用前需要指定outputFile。
 * 换句话说，想要录制视频，在创建session时，就需要指定录制文件。
 * 但是，我们一般会有在一次预览中，录制多个视频文件的需求，这就要求我们每次录制时，都要重建Camera
 *
 * 如果我们的app最低支持的系统版本>=23，则可以通过MediaCodec.createPersistentInputSurface()方法提前创建Surface。
 * 当我们需要录制视频时，再创建MediaRecorder，并调用MediaRecorder.setInputSurface(Surface)
 *
 * 如果我们的app最低支持的系统版本<23，则需要使用MediaCodec+AudioRecord+MediaMuxer
 */

class Camera2Fragment : Fragment(), ImageReader.OnImageAvailableListener {
    private lateinit var binding: FragmentCamera2Binding

    private var facing = CameraCharacteristics.LENS_FACING_BACK
    private var width = 1920
    private var height = 1080

    private var camera: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null

    private lateinit var previewSurface: Surface
    private lateinit var recordSurface: Surface

    private lateinit var encoder: MediaCodec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
//        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
//        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
//        format.setInteger(
//            MediaFormat.KEY_COLOR_FORMAT,
//            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
//        )
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)
//        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//        encoder.setCallback(encoderCallback)
//        recordSurface = encoder.createInputSurface()
    }

    override fun onDestroy() {
        super.onDestroy()
        encoder.reset()
        encoder.release()
    }

    private val encoderCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {

        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec,
            index: Int,
            info: MediaCodec.BufferInfo
        ) {
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
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
        binding.imageCaptureBtn.setOnClickListener {
            takePhoto()
        }
        binding.switchCameraBtn.setOnClickListener {
//            switchCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.textureView.isAvailable) {
            val cameraId = requireContext().getCameraId(facing)
            val texture = binding.textureView.surfaceTexture!!
            openCamera(cameraId, texture)
        }
        binding.textureView.surfaceTextureListener = surfaceTextureListener
    }

    private val surfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            previewSurface = Surface(surface)

            val cameraId = requireContext().getCameraId(facing)
            openCamera(cameraId, surface, width, height)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            closeCamera()
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera(
        cameraId: String,
        texture: SurfaceTexture,
        width: Int = this.width,
        height: Int = this.height
    ) {
        val manager = requireContext().getSystemService(CAMERA_SERVICE) as CameraManager
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                this@Camera2Fragment.camera = camera
                createCameraPreviewSession(camera, texture)
            }

            override fun onClosed(camera: CameraDevice) {
                Log.d(TAG, "Camera closed!")
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "Camera onDisconnected!")
                closeCamera()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "Camera onError: $error")
                closeCamera()
            }

        }, null)
    }

    private fun createCameraPreviewSession(camera: CameraDevice, texture: SurfaceTexture) {
        val manager = requireContext().getSystemService(CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(camera.id)

        //获取摄像头朝向
        val isFrontCamera =
            characteristics[CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_FRONT
        val cameraOrientation = characteristics[CameraCharacteristics.SENSOR_ORIENTATION]!!
        val screenRotation = requireActivity().getDisplayRotation()
        val phoneRotation = requireActivity().getDisplayRotation()

        //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
        val map = characteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]!!
        val format = ImageFormat.JPEG
        val outputSize = map.getOutputSizes(format).maxBy { it.width * it.height }
        val imageReader = ImageReader.newInstance(outputSize.width, outputSize.height, format, 2)
        imageReader.setOnImageAvailableListener(this, null)

        val surface = Surface(texture)
        val outputs = listOf(surface, recordSurface, imageReader.surface)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val outputConfigurations = outputs.map { output ->
                OutputConfiguration(output)
            }
            val config = SessionConfiguration(
                SESSION_REGULAR,
                outputConfigurations,
                ContextCompat.getMainExecutor(requireContext()),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        this@Camera2Fragment.captureSession = session
                        this@Camera2Fragment.imageReader = imageReader

                        val previewRequest =
                            camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                                addTarget(surface)
                            }.build()
                        session.setRepeatingRequest(previewRequest, null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                })
            camera.createCaptureSession(config)
        } else {
            camera.createCaptureSession(
                outputs,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        this@Camera2Fragment.captureSession = session
                        this@Camera2Fragment.imageReader = imageReader

                        val previewRequest =
                            camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                                addTarget(surface)
                            }.build()
                        session.setRepeatingRequest(previewRequest, null, null)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {

                    }
                }, null
            )
        }
    }

    private fun closeCamera() {
        camera?.close()
        camera = null
    }

    private fun takePhoto() {
        val camera = camera ?: return
        val imageReader = imageReader ?: return
        val captureRequest = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            .apply {
                addTarget(imageReader.surface)
                set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
                set(CaptureRequest.JPEG_ORIENTATION, 90)
            }.build()
        captureSession?.stopRepeating()
        captureSession?.abortCaptures()
        captureSession?.capture(captureRequest, object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
            }
        }, null)
    }

    companion object {
        private const val TAG = "Camera2Fragment"
    }

    override fun onImageAvailable(reader: ImageReader) {
        val image = reader.acquireLatestImage()
        image.close()
    }
}

private fun Context.getCameraId(facing: Int, default: String = "0"): String {
    val manager = getSystemService(CAMERA_SERVICE) as CameraManager
    return manager.cameraIdList.firstOrNull { cameraId ->
        val characteristics = manager.getCameraCharacteristics(cameraId)
        characteristics[CameraCharacteristics.LENS_FACING] == facing
    } ?: default
}