package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import game12.client.gui.*;

public class ThirdPersonGui extends Gui {

	private GLabel roundLabel;
	private GLabel timeLabel;

	private GPanel  buildPanel;
	private GButton roomButton;
	private GButton doorButton;

	public ThirdPersonGui(EventManager eventManager, MapBuilder mapBuilder) {
		super(eventManager);

		roundLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(roundLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 20);

		timeLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(timeLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 70);

		buildPanel = new GPanel(GuiConstants.GUI_BACKGROUND_COLOR, Integer.MAX_VALUE, 50);
		addElement(buildPanel, ALIGNMENT_LEFT, ALIGNMENT_BOTTOM, 0, 0);

		roomButton = new GButton(GuiConstants.DEFAULT_FONT, "room", 100, 20, mapBuilder::roomButton);
		roomButton.setColors(GuiConstants.FONT_COLOR, GuiConstants.FONT_HOVER_COLOR);
		buildPanel.addElement(roomButton, ALIGNMENT_LEFT, ALIGNMENT_BOTTOM, 50, 20);

		doorButton = new GButton(GuiConstants.DEFAULT_FONT, "door", 100, 20, mapBuilder::doorButton);
		doorButton.setColors(GuiConstants.FONT_COLOR, GuiConstants.FONT_HOVER_COLOR);
		buildPanel.addElement(doorButton, ALIGNMENT_LEFT, ALIGNMENT_BOTTOM, 200, 20);

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
