package game12.client.controller.firstPerson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.util.Color;
import game12.client.gui.GLabel;
import game12.client.gui.Gui;
import game12.client.gui.GuiConstants;

public class FirstPersonGui extends Gui {

	public FirstPersonGui(EventManager eventManager) {
		super(eventManager);

		GLabel label = new GLabel(GuiConstants.DEFAULT_FONT, Color.BLACK, "Lorem ipsum dolor sit amet.");
		addElement(label, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 0, 0);
	}

}
