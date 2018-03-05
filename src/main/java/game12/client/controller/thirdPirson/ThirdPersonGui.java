package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.util.Color;
import game12.client.gui.GLabel;
import game12.client.gui.Gui;
import game12.client.gui.GuiConstants;

public class ThirdPersonGui extends Gui {

	private GLabel roundLabel;
	private GLabel timeLabel;

	public ThirdPersonGui(EventManager eventManager) {
		super(eventManager);

		GLabel label = new GLabel(GuiConstants.DEFAULT_FONT, Color.BLACK, "Lorem ipsum dolor sit amet.");
		addElement(label, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 0, 0);

		roundLabel = new GLabel(GuiConstants.DEFAULT_FONT, Color.WHITE, "");
		addElement(roundLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 20);

		timeLabel = new GLabel(GuiConstants.DEFAULT_FONT, Color.WHITE, "");
		addElement(timeLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 70);
	}

	public void setCurrentRoom(int roomId) {
		roundLabel.setText("Room: " + roomId);
	}

	public void setTime(float time) {
		int seconds = (int) time;
		int minutes = seconds / 60;
		seconds -= minutes * 60;

		if (minutes > 0) {
			timeLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
		} else {
			timeLabel.setText(String.format("Time: %02d", seconds));
		}

	}

}
