package com.pujh.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OpenGLFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_opengl, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //使用OpenGLES的版本
        val glSurfaceView = view.findViewById<GLSurfaceView>(R.id.gl_surface_view)
        glSurfaceView.setEGLContextClientVersion(1)
//        glSurfaceView.setRenderer(CCGLRender())
        glSurfaceView.setRenderer(CCGLRender2(requireContext()))
    }

}