package com.pujh.opengl.camera.filter

import android.content.Context
import android.content.res.AssetManager
import com.pujh.opengl.camera.Filter

class NativeFilter(
    private val context: Context
) : Filter {

    override fun init(oesTexIdx: Int) {
        native_onFilterInit(context.assets, oesTexIdx)
    }

    override fun windowSizeChanged(width: Int, height: Int) {
        native_onWindowSizeChanged(width, height)
    }

    override fun streamSizeChanged(width: Int, height: Int) {

    }

    override fun draw() {
        native_onFilterDraw()
    }

    override fun destroy() {
        native_onFilterDestroy()
    }

    private external fun native_onFilterInit(assetManager: AssetManager, oesTexIdx: Int)
    private external fun native_onWindowSizeChanged(width: Int, height: Int)
    private external fun native_onFilterDraw()
    private external fun native_onFilterDestroy()
}