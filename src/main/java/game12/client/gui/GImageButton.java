package game12.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.*;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.Matrix4f;
import game12.client.map.TerrainTexture;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class GImageButton extends GElementContainer {

	private Runnable function;
	private boolean  clickStarted;
	private boolean  pressed;

	private              Color              color;
	private static final VertexBufferObject quad;
	private static final Shader             shader;
	private              Texture2D          texture;

	private static final float HOVER_ANIMATION_TIME     = 0.08f;
	private static final float HOVER_ANIMATION_DISTANCE = -10f;
	private              float hoverAnimationProgress   = 0;
	private              float hoverAnimationOffset     = 0;

	private static final float CLICK_ANIMATION_TIME         = 0.06f;
	private static final float CLICK_ANIMATION_DISTANCE     = 0.15f;
	private              float clickAnimationTargetProgress = 0;
	private              float clickAnimationProgress       = 0;
	private              float clickAnimationOffset         = 0;

	public GImageButton(Texture2D texture, Color color, int width, Runnable function) {
		this.texture = texture;
		this.color = color;
		this.width = width;
		this.height = width;

		this.function = function;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {

		if (mouseInBounds(inputHandler)) {

			for (MouseButtonEvent mouseButtonEvent : inputHandler.getMouseButtonEvents()) {
				if (mouseButtonEvent.button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseButtonEvent.action == GLFW.GLFW_PRESS) {
					clickStarted = true;
					clickAnimationTargetProgress = 1;

					mouseButtonEvent.setProcessed();
				} else if (mouseButtonEvent.button == GLFW.GLFW_MOUSE_BUTTON_LEFT && mouseButtonEvent.action == GLFW.GLFW_RELEASE) {
					if (clickStarted) {
						function.run();
					}
					clickStarted = false;
					clickAnimationTargetProgress = 0;

					mouseButtonEvent.setProcessed();
				}
			}
		} else {
			clickStarted = false;
			clickAnimationTargetProgress = 0;
		}

		{
			float targetProgress = 0;
			if (mouseInBounds(inputHandler)) targetProgress = 1;
			if (pressed) targetProgress = 1; // fix animation if button is pressed
			float animationDelta = timeDelta / HOVER_ANIMATION_TIME;
			hoverAnimationProgress = hoverAnimationProgress + Math.signum(targetProgress - hoverAnimationProgress) * animationDelta;
			hoverAnimationProgress = Math.max(0, Math.min(1, hoverAnimationProgress));

			// position = -x^2 + 2x
			hoverAnimationOffset = HOVER_ANIMATION_DISTANCE * (-hoverAnimationProgress * hoverAnimationProgress + 2 * hoverAnimationProgress);
		}

		{
			float animationDelta = timeDelta / CLICK_ANIMATION_TIME;
			clickAnimationProgress = clickAnimationProgress + Math.signum(clickAnimationTargetProgress - clickAnimationProgress) * animationDelta;
			clickAnimationProgress = Math.max(0, Math.min(1, clickAnimationProgress));
			// position = -x^2 + 2x
			clickAnimationOffset = CLICK_ANIMATION_DISTANCE * (-clickAnimationProgress * clickAnimationProgress + 2 * clickAnimationProgress);
		}

	}

	@Override
	public void render(Matrix4f projectionMatrix) {

		texture.bind(0);

		float renderSize = width * (1f - clickAnimationOffset);

		shader.activate();
		shader.setUniformMat4f("projectionMatrix", projectionMatrix.asBuffer());
		shader.setUniform2f(
				"position",
				posX + hoverAnimationOffset + (clickAnimationOffset * width / 2),
				posY + (clickAnimationOffset * height / 2)
		                   );
		shader.setUniform2f("size", renderSize, renderSize);
		shader.setUniform4f("color", color.getR(), color.getG(), color.getB(), color.getA());
		shader.setUniform1i("textureColor", 0);

		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA, GL_ONE);

		quad.render();

		glDisable(GL_BLEND);

		shader.deactivate();

		super.render(projectionMatrix);
	}

	static {
		quad = new VertexBufferObjectStandard(
				new int[] { 2, 2 },
				new float[] { // position
						0, 0,
						1, 0,
						1, 1,

						0, 0,
						1, 1,
						0, 1,
				},
				new float[] { // texture
						0, 0,
						1, 0,
						1, 1,

						0, 0,
						1, 1,
						0, 1,
				}
		);

		shader = ShaderLoader.loadShader(
				"shaders/gui/imageButton/imageButton.vert",
				"shaders/gui/imageButton/imageButton.frag"
		                                );

	}

}
