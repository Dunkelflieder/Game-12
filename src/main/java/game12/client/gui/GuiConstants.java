package game12.client.gui;

import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;

public class GuiConstants {

	public static Font DEFAULT_FONT;

	public static Color GUI_BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.5f);

	public static Color FONT_COLOR = new Color(0.7f, 0.7f, 0.7f, 1.0f);
	public static Color FONT_HOVER_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);

	public static void init() {
		DEFAULT_FONT = new Font("Consolas", 40);
	}

}
