layout (location = 5) in vec3 colorAttrib;

uniform float u_tileSize;

out vec3 var_color;

void mainSurface(inout vec2 uv, inout vec3 position, inout vec3 normal) {
	uv *= u_tileSize;

	var_color = colorAttrib;
}
