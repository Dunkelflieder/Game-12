#version 330 core

uniform vec4 color;
uniform vec2 size;
uniform float time;
uniform float panelWidth;
uniform float panelBlendOut;
uniform float backgroundBlendOut;

layout (location = 0) out vec4 frag_color;

in DATA
{
	vec2 position;
} frag_in;

#define INVERSE_TRIANGLE_HEIGHT    1.1547005384

#define INVERSE_TRIANGLE_SIZE      0.02
#define CENTER1                    vec2(1.0 / 3.0)
#define CENTER2                    (vec2(1.0) - CENTER1)

#define LINE_WIDTH                 0.2
#define INVERSE_LINE_WIDTH         5.0

float distanceField(vec2 pos, out vec2 center) {
	vec2 localPosition = vec2(pos.x - pos.y * (0.5 * INVERSE_TRIANGLE_HEIGHT), pos.y * INVERSE_TRIANGLE_HEIGHT) * INVERSE_TRIANGLE_SIZE;
	vec2 trianglePosition = fract(localPosition);
    vec2 cornerPosition = localPosition - trianglePosition;

	vec2 distV1 = CENTER1 - trianglePosition;
	float distDiag1 = ((trianglePosition.x + trianglePosition.y) - (CENTER1.x + CENTER1.y));
	vec2 distV2 = trianglePosition - CENTER2;
	float distDiag2 = ((CENTER2.x + CENTER2.y) - (trianglePosition.x + trianglePosition.y));

	float dist1 = max(max(distV1.x, distV1.y), distDiag1);
	float dist2 = max(max(distV2.x, distV2.y), distDiag2);

    if ((distV1.x * distV1.x + distV1.y * distV1.y) < (distV2.x * distV2.x + distV2.y * distV2.y)){
    	center = CENTER1 + cornerPosition;
    } else {
    	center = CENTER2 + cornerPosition;
    }

	return min(dist1, dist2) * 3.0;
}

float linePosition(vec2 pos) {
	float offset = sin(pos.x + time * 2.0) * 0.05;
    return 0.6 + offset;
}

vec2 factorFunction(float dist, float linePosition, bool afterStageBlendOut) {

	float triangleFactor = 1.0 - abs(((dist - linePosition) * 2.0 * INVERSE_LINE_WIDTH - 1.0));
	float transparencyFactor;

	if (afterStageBlendOut) {
		if (dist > linePosition + (0.5 * LINE_WIDTH)) {
			transparencyFactor = triangleFactor;
		} else {
			transparencyFactor = linePosition + LINE_WIDTH;
		}
	} else {
		transparencyFactor = 1.0;
	}

	return vec2(triangleFactor, transparencyFactor);
}

bool afterStageBlendOutFunction(float localBlendOut, float stageTime) {
	return localBlendOut > stageTime;
}

float blendOutSize(vec2 pos, float linePosition, out bool afterStageBlendOut) {
	const float stageTime = 0.3;
	const float blendOutWidth = 12.0;

	vec2 gridSize = size * INVERSE_TRIANGLE_SIZE;
	float gridBlendOut = backgroundBlendOut * INVERSE_TRIANGLE_SIZE;

	float blendOutPosition = gridSize.x - gridBlendOut;
	float blendOutProgress = (pos.x - blendOutPosition) / blendOutWidth;

	float localBlendOut = clamp(blendOutProgress, 0.0, 1.0);

	afterStageBlendOut = afterStageBlendOutFunction(localBlendOut, stageTime);

	if (afterStageBlendOut) {
		return (mix(1.0, 0.0, (localBlendOut - stageTime) / (1.0 - stageTime)) - (1.0 / INVERSE_LINE_WIDTH));
	} else {
		return mix(linePosition, (1.0 - (1.0 / INVERSE_LINE_WIDTH)), localBlendOut / stageTime);
	}
}

void main(){
	vec3 color = vec3(0.04, 0.06, 0.2) * 0.6;

    vec2 center;
	float dist = distanceField(frag_in.position, center);

	bool afterStageBlendOut;
	float blendOutSize = blendOutSize(center, linePosition(center), afterStageBlendOut);
	vec2 factor = factorFunction(dist, blendOutSize, afterStageBlendOut);

	float panelFactor = mix(0.5 + panelBlendOut * 5.0, 5.5, clamp((frag_in.position.x - panelWidth) / (2.0 * panelWidth), 0.0, 1.0));

	frag_color = vec4(color * factor.x * panelFactor, factor.y);
}
