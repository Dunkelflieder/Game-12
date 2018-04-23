package game12.client.gui;

import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.KeyboardKeyEvent;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;
import org.lwjgl.glfw.GLFW;

/**
 * TODO: Implement GUI-focus, otherwise you can only ever have 1 input field always accepting input
 */
public class GTextInput extends GPanel {

	private final GLabel inputText;

	public GTextInput(Font font, Color color, String defaultText, int width, int height) {
		super(color, width, height + 10);
		inputText = new GLabel(font, color, defaultText);
		addElement(inputText, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, 0, 0);
		setColor(new Color(0x99333333));
	}

	@Override
	protected void recalculatePosition(int parentX, int parentY, int parentWidth, int parentHeight) {
		super.recalculatePosition(parentX, parentY, parentWidth, parentHeight);
	}

	private void pressBackspace() {
		String text = inputText.getText();
		if (!text.isEmpty()) {
			inputText.setText(text.substring(0, text.length() - 1));
		}
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
		super.processInput(inputHandler, timeDelta);

		for (KeyboardKeyEvent event : inputHandler.getKeyboardKeyEvents()) {
			if (event.action != GLFW.GLFW_PRESS && event.action != GLFW.GLFW_REPEAT) {
				continue;
			}
			if (event.key == GLFW.GLFW_KEY_BACKSPACE) {
				pressBackspace();
			}
		}

		String newText = inputHandler.getInputText();
		if (!newText.isEmpty()) {
			inputText.setText(inputText.getText() + newText);
		}

	}

	public String getInputText() {
		return inputText.getText();
	}
}
