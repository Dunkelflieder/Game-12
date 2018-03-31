package game12.client.gui;

import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.MaterialDesignColors;
import de.nerogar.noise.util.Matrix4f;

public class GProgressbar extends GPanel {

	public interface LabelFormatter {

		String format(int value, int maxValue);
	}

	private static final Color DEFAULT_COLOR_BG       = MaterialDesignColors.GREY_900;
	private static final Color DEFAULT_COLOR_FG       = MaterialDesignColors.RED_A700;
	private static final Color DEFAULT_COLOR_FG_BLINK = MaterialDesignColors.RED_A200;

	private static final Color DEFAULT_COLOR_TEXT = MaterialDesignColors.GREY_200;
	private              Color colorBg            = DEFAULT_COLOR_BG;
	private              Color colorFg            = DEFAULT_COLOR_FG;
	private              Color colorFgBlink       = DEFAULT_COLOR_FG_BLINK;

	private Color colorText = DEFAULT_COLOR_TEXT;
	private Font  font;
	private int   maxValue;
	private int   value;

	private boolean blinking;

	private LabelFormatter labelFormatter;

	private final GPanel innerPanel;
	private final GLabel innerText;
	private       int    renderCounter;

	public GProgressbar(int width, int height, int maxValue, LabelFormatter labelFormatter) {
		super(Color.TRANSPARENT, width, height);
		this.font = new Font("Consolas", (int) (height * 0.9f));
		this.maxValue = maxValue;
		this.value = maxValue;
		this.labelFormatter = labelFormatter;

		innerPanel = new GPanel(colorFg, width, height);
		addElement(innerPanel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_CENTER, 0, 0);
		innerText = new GLabel(font, colorText, "");
		addElement(innerText, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_TOP, 0, -height / 10);
		updateContents();
		updateColors();
	}

	@Override
	public void render(Matrix4f projectionMatrix) {
		super.render(projectionMatrix);
		if (blinking && renderCounter % 2 == 0) {
			innerPanel.setColor(colorFgBlink);
		} else {
			innerPanel.setColor(colorFg);
		}
		renderCounter++;
	}

	private void updateContents() {
		float percentage = value / (float) maxValue;
		innerPanel.setSize((int) (getWidth() * percentage), getHeight());
		innerText.setText(labelFormatter.format(value, maxValue));
		innerText.recalculatePosition(posX, posY, getWidth(), getHeight());
	}

	private void updateColors() {
		super.setColor(colorBg);
		innerPanel.setColor(colorFg);
		innerText.setColor(colorText);
	}

	public void setColors(Color bg, Color fg, Color fgBlink, Color text) {
		colorBg = bg;
		colorFg = fg;
		colorFgBlink = fgBlink;
		colorText = text;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		updateContents();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		updateContents();
	}

	public boolean isBlinking() {
		return blinking;
	}

	public void setBlinking(boolean blinking) {
		this.blinking = blinking;
	}
}
