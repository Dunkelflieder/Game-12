package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.render.Texture2DLoader;
import de.nerogar.noise.util.Color;
import game12.client.gui.*;

import java.util.ArrayList;
import java.util.List;

public class ThirdPersonGui extends Gui {

	private static final int BUTTON_SIZE    = 50;
	private static final int BUTTON_PADDING = 20;
	private GLabel roundLabel;
	private GLabel timeLabel;

	private GPanel             buildPanel;
	private List<GImageButton> buildPanelButtons;

	public ThirdPersonGui(EventManager eventManager, MapBuilder mapBuilder) {
		super(eventManager);

		roundLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(roundLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 20);

		timeLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(timeLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 70);

		buildPanel = new GPanel(GuiConstants.GUI_BACKGROUND_COLOR, 120, 1000000);
		addElement(buildPanel, ALIGNMENT_RIGHT, ALIGNMENT_TOP, 0, 0);
		buildPanelButtons = new ArrayList<>();

		createBuildPanel(mapBuilder);
	}

	private void createBuildPanel(MapBuilder mapBuilder) {
		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/gui/roomButton.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(0);
					mapBuilder.roomButton();
				}
		));

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/gui/doorButton.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(1);
					mapBuilder.doorButton();
				}
		));

		for (int i = 0; i < buildPanelButtons.size(); i++) {
			buildPanel.addElement(
					buildPanelButtons.get(i),
					ALIGNMENT_LEFT,
					ALIGNMENT_TOP,
					BUTTON_PADDING,
					BUTTON_PADDING + (BUTTON_PADDING + BUTTON_SIZE) * i
			                     );
		}
	}

	public void activateButton(int id) {
		for (GImageButton buildPanelButton : buildPanelButtons) {
			buildPanelButton.setPressed(false);
		}

		buildPanelButtons.get(id).setPressed(true);
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
