#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 position;
uniform vec2 size;
uniform vec2 textureOffset;

layout (location = 0) in vec2 vert_position;
layout (location = 1) in vec2 vert_uv;
layout (location = 2) in float brightness;

out DATA
{
	vec2 uv;
	float brightness;
} vert_out;

void main(){
	gl_Position = projectionMatrix * vec4(vert_position * size + position, 0.0, 1.0);

	vert_out.uv = vert_uv + textureOffset;
	vert_out.brightness = brightness;
}
