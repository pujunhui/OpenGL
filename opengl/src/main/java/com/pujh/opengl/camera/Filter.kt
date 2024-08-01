package com.pujh.opengl.camera

interface Filter {
    fun init(oesTexIdx: Int)

    fun windowSizeChanged(width: Int, height: Int)

    fun streamSizeChanged(width: Int, height: Int)

    fun draw()

    fun destroy()

}