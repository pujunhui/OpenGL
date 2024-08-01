#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 texCoord;
out vec4 outColor;
uniform samplerExternalOES s_TextureMap;
void main() {
    outColor = texture(s_TextureMap, texCoord);
}