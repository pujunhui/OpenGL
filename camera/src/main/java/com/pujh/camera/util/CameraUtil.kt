package com.pujh.camera.util

import android.graphics.Matrix
import android.hardware.Camera
import android.util.Log
import android.util.Size
import kotlin.math.abs

private const val TAG = "CameraUtil"

fun getOptimalPreviewSize(
    sizeList: List<Camera.Size>,
    cameraRotate: Int,
    displaySize: Size,
): Size {
    val suggestWidth: Int
    val suggestHeight: Int
    if (cameraRotate == 90 || cameraRotate == 270) {
        suggestWidth = displaySize.height
        suggestHeight = displaySize.width
    } else {
        suggestWidth = displaySize.width
        suggestHeight = displaySize.height
    }

    val aspectTolerance = 0.1
    val targetRatio = suggestWidth / suggestHeight.toFloat()

    var optimalSize: Size? = null

    var minDiff = Float.MAX_VALUE

    val targetHeight: Int = suggestHeight

    // Try to find an size match aspect ratio and size
    for (size in sizeList) {
        val ratio = size.width / size.height.toFloat()
        if (abs(ratio - targetRatio) > aspectTolerance) {
            continue
        }
        if (abs(size.height - targetHeight) < minDiff) {
            optimalSize = Size(size.width, size.height)
            minDiff = abs(size.height - targetHeight).toFloat()
        }
    }

    // Cannot find the one match the aspect ratio, ignore the requirement
    if (optimalSize == null) {
        minDiff = Float.MAX_VALUE
        for (size in sizeList) {
            if (abs((size.height - targetHeight).toDouble()) < minDiff) {
                optimalSize = Size(size.width, size.height)
                minDiff = abs(size.height - targetHeight).toFloat()
            }
        }
    }

    if (optimalSize == null) {
        optimalSize = Size(1920, 1080)
    }
    return optimalSize
}

enum class ScaleType {
    FIT_XY,  //不保持宽高比，拉伸填充
    CENTER,  //保持原大小，居中显示
    CENTER_CROP,  //保持宽高比，居中，同时满足任意方向填充满，不会留空白
    CENTER_INSIDE,  //保持宽高比，居中，满足任意方向填充满即可，可能会留空白
}

/**
 * Camera1+TextureView进行预览时，默认采用全铺满的方式进行显示，
 * 并且提供Camera#setDisplayOrientation方法设置画面旋转角度，但仍需要我们自己处理缩放、位移、镜像等操作。
 * 而此方法，可以获得一个Matrix，传入TextureView#setTransform方法中，一次完成旋转、缩放、位移、镜像等操作。
 * 注意：必须手动设置camera.setDisplayOrientation(0)。
 *
 * 矩阵计算思路:
 *   1、同时旋转camera帧和surface
 *   2、计算surface的宽高缩放比，让旋转后的camera帧满足显示要求
 *   3、通过display裁剪surface区域
 *
 * 其中涉及的变量：
 * contentSize，camera frame旋转后的尺寸
 * surfaceSize，display旋转后的尺寸，最终可绘制的区域是surface和display交集
 *
 * @param cameraRotate camera sensor角度
 * @param cameraSize camera预览尺寸（camera数据流大小）
 * @param displaySize 显示控件（TextureView）大小
 * @param scaleType 缩放方式
 * @param mirror 对显示画面进行额外水平镜像，一般默认false即可。如果是前置摄像头，并且不希望镜像显示，则可设置为true
 */
fun getCameraMatrix(
    cameraRotate: Int,
    cameraSize: Size,
    displaySize: Size,
    scaleType: ScaleType = ScaleType.CENTER_CROP,
    mirror: Boolean = false
): Matrix {
    val matrix = Matrix()

    val contentWidth: Int
    val contentHeight: Int
    val surfaceWidth: Int
    val surfaceHeight: Int
    if (cameraRotate == 90 || cameraRotate == 270) {
        contentWidth = cameraSize.height
        contentHeight = cameraSize.width
        surfaceWidth = displaySize.height
        surfaceHeight = displaySize.width
    } else {
        contentWidth = cameraSize.width
        contentHeight = cameraSize.height
        surfaceWidth = displaySize.width
        surfaceHeight = displaySize.height
    }
    val scaleX: Float
    val scaleY: Float
    when (scaleType) {
        ScaleType.FIT_XY -> {
            scaleX = displaySize.width / surfaceWidth.toFloat()
            scaleY = displaySize.height / surfaceHeight.toFloat()
        }

        ScaleType.CENTER -> {
            // P * (surfaceSize / contentSize) * scale = P`
            // 当P = P`时， scale = contentSize / surfaceSize
            scaleX = contentWidth / surfaceWidth.toFloat()
            scaleY = contentHeight / surfaceHeight.toFloat()
        }

        ScaleType.CENTER_CROP -> {
            val displayRatio = displaySize.width / displaySize.height.toFloat()
            val contentRatio = contentWidth / contentHeight.toFloat()
            val scale = if (displayRatio > contentRatio) {
                displaySize.width / contentWidth.toFloat()
            } else {
                displaySize.height / contentHeight.toFloat()
            }
            //求得共同缩放比后，再乘各自的反形变缩放比，从而避免变形
            scaleX = scale * contentWidth / surfaceWidth.toFloat()
            scaleY = scale * contentHeight / surfaceHeight.toFloat()
        }

        ScaleType.CENTER_INSIDE -> {
            val displayRatio = displaySize.width / displaySize.height.toFloat()
            val contentRatio = contentWidth / contentHeight.toFloat()
            val scale = if (displayRatio > contentRatio) {
                displaySize.height / contentHeight.toFloat()
            } else {
                displaySize.width / contentWidth.toFloat()
            }
            //求得共同缩放比后，再乘各自的反形变缩放比，避免变形
            scaleX = scale * contentWidth / surfaceWidth.toFloat()
            scaleY = scale * contentHeight / surfaceHeight.toFloat()
        }
    }

    Log.d(TAG, "scaleX=$scaleX, scaleY=$scaleY, rotate=$cameraRotate")

    val centerX = displaySize.width / 2f
    val centerY = displaySize.height / 2f
    matrix.postRotate(cameraRotate.toFloat(), centerX, centerY)
    if (mirror) {
        matrix.postScale(-scaleX, scaleY, centerX, centerY)
    } else {
        matrix.postScale(scaleX, scaleY, centerX, centerY)
    }
    return matrix
}

/**
 * @param isFrontCamera 是否是前置摄像头，如果是则需要水平镜像画面
 * @param cameraOrientation camera sensor角度
 * @param displayRotation 显示方向
 */
fun getCameraRotate(
    isFrontCamera: Boolean,
    cameraOrientation: Int,
    displayRotation: Int
): Int {
    var result: Int
    if (isFrontCamera) {
        result = (cameraOrientation + displayRotation) % 360 //270+0
        result = (360 - result) % 360  // compensate the mirror
    } else { // back-facing
        result = (cameraOrientation - displayRotation + 360) % 360
    }
    return result
}

/**
 * @param isFrontCamera 是否是前置摄像头，如果是则需要水平镜像画面
 * @param cameraOrientation camera sensor角度
 * @param displayRotation 显示方向
 */
fun getCamera2Rotate(
    isFrontCamera: Boolean,
    cameraOrientation: Int,
    displayRotation: Int
): Int {
    return (360 - displayRotation)
}