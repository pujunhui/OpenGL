package com.pujh.opengl.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pujh.opengl.basic.model.TextureRenderOrtho
import com.pujh.opengl.basic.model.TextureRenderOrtho1
import com.pujh.opengl.databinding.FragmentBasicBinding

/**
 * OpenGL Java/Native基础示例
 */
class BasicFragment : Fragment() {
    private lateinit var binding: FragmentBasicBinding
    private lateinit var basicRender: BasicRender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        basicRender = BasicRender(TextureRenderOrtho1(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //使用OpenGLES的版本
        binding.glSurfaceView.setEGLContextClientVersion(3)
        binding.glSurfaceView.setRenderer(basicRender)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        basicRender.onDestroy()
    }
}