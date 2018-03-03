#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 position;
uniform vec2 size;

layout (location = 0) in vec2 vert_position;

out DATA
{
	vec2 position;
} vert_out;

void main(){
	vert_out.position = vert_position * size;
	gl_Position = projectionMatrix * vec4(vert_position * size + position, 0.0, 1.0);
}
