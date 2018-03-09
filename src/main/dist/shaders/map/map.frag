in vec3 var_color;

void mainSurface(inout vec4 color, in vec2 uv, in vec3 position, inout vec3 normal, inout float displace, inout vec4 light) {
	//vec3 tile = abs(fract(position) - vec3(0.5));

	//color = mix(color, vec4(var_color, 1.0), 0.2);
	color = color + vec4(var_color, 1.0) * 0.3;
}
