package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.util.Color;
import game12.client.gui.GLabel;
import game12.client.gui.Gui;
import game12.client.gui.GuiConstants;

public class ThirdPersonGui extends Gui {

	public ThirdPersonGui(EventManager eventManager) {
		super(eventManager);

		GLabel label = new GLabel(GuiConstants.DEFAULT_FONT, Color.CYAN, "Lorem ipsum dolor sit amet.");
		addElement(label, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 0, 0);
	}

}
