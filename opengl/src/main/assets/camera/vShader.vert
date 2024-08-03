#version 300 es
layout(location = 0) in vec4 vPosition;
layout(location = 1) in vec2 vTexCoord;
out vec2 texCoord;
void main()
{
    gl_Position = vPosition;
    texCoord = vTexCoord;
}