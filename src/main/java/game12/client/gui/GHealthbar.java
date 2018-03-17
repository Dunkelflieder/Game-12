package game12.client.gui;

import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.MaterialDesignColors;
import de.nerogar.noise.util.Matrix4f;

public class GHealthbar extends GPanel {

	private static final Color COLOR_BG       = MaterialDesignColors.GREY_900;
	private static final Color COLOR_FG       = MaterialDesignColors.RED_A700;
	private static final Color COLOR_FG_BLINK = MaterialDesignColors.RED_A200;
	private static final Color COLOR_TEXT     = MaterialDesignColors.GREY_200;
	private static       Font  FONT           = null;

	private int maxHealth;
	private int health;
	private boolean blinking = true;

	private final GPanel innerPanel;
	private final GLabel innerText;
	private       int    renderCounter;

	public GHealthbar(int width, int height, int maxHealth) {
		super(COLOR_BG, width, height);
		if (FONT == null) {
			FONT = new Font("Consolas", (int) (height * 0.9f));
		}
		this.maxHealth = maxHealth;
		this.health = maxHealth;

		innerPanel = new GPanel(COLOR_FG, width, height);
		addElement(innerPanel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_CENTER, 0, 0);
		innerText = new GLabel(FONT, COLOR_TEXT, "");
		addElement(innerText, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_TOP, 0, -height / 10);
		update();
	}

	@Override
	public void render(Matrix4f projectionMatrix) {
		super.render(projectionMatrix);
		if (blinking && renderCounter % 2 == 0) {
			innerPanel.setColor(COLOR_FG_BLINK);
		} else {
			innerPanel.setColor(COLOR_FG);
		}
		renderCounter++;
	}

	private void update() {
		float percentage = health / (float) maxHealth;
		innerPanel.setSize((int) (getWidth() * percentage), getHeight());
		innerText.setText(health + "/" + maxHealth);
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		update();
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		update();
	}

	public boolean isBlinking() {
		return blinking;
	}

	public void setBlinking(boolean blinking) {
		this.blinking = blinking;
	}
}