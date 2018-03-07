#version 330 core

uniform vec4 color;
uniform sampler2D textureColor;

layout (location = 0) out vec4 frag_color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){

	frag_color = texture(textureColor, frag_in.uv) * color;

}
