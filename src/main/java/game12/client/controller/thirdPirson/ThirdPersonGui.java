package game12.client.controller.thirdPirson;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.input.MouseButtonEvent;
import de.nerogar.noise.render.Texture2DLoader;
import de.nerogar.noise.util.Color;
import de.nerogar.noise.util.MaterialDesignColors;
import game12.client.gui.*;
import game12.client.map.ClientMap;
import game12.core.systems.GameProgressSystem;
import game12.core.systems.PlayerSystem;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ThirdPersonGui extends Gui {

	private static final int BUTTON_SIZE    = 50;
	private static final int BUTTON_PADDING = 20;

	private static final Color PROGRESS_COLOR_BG       = MaterialDesignColors.GREY_900;
	private static final Color PROGRESS_COLOR_FG       = MaterialDesignColors.GREEN_A700;
	private static final Color PROGRESS_COLOR_FG_BLINK = MaterialDesignColors.GREEN_A200;
	private static final Color PROGRESS_COLOR_TEXT     = MaterialDesignColors.GREY_200;

	private final MapBuilder mapBuilder;

	private GLabel roundLabel;
	private GLabel timeLabel;

	private GProgressbar timeProgressbar;

	private GPanel             buildPanel;
	private List<GImageButton> buildPanelButtons;

	private final GProgressbar healthbar;

	public ThirdPersonGui(ClientMap map, EventManager eventManager, MapBuilder mapBuilder) {
		super(eventManager);
		this.mapBuilder = mapBuilder;

		roundLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(roundLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 20);

		timeProgressbar = new GProgressbar(500, 30, (int) GameProgressSystem.ROOM_TIME, (value, maxValue) -> formatTime(value));
		timeProgressbar.setColors(PROGRESS_COLOR_BG, PROGRESS_COLOR_FG, PROGRESS_COLOR_FG_BLINK, PROGRESS_COLOR_TEXT);
		addElement(timeProgressbar, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_TOP, 0, 20);
		timeLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, "");
		addElement(timeLabel, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 20, 70);

		buildPanel = new GPanel(GuiConstants.GUI_BACKGROUND_COLOR, 120, 1000000);
		addElement(buildPanel, ALIGNMENT_RIGHT, ALIGNMENT_TOP, 0, 0);
		buildPanelButtons = new ArrayList<>();

		PlayerSystem playerSystem = map.getSystem(PlayerSystem.class);

		healthbar = new GProgressbar(500, 20, playerSystem.getPlayerHealth().maxHealth, (value, maxValue) -> value + "/" + maxValue);
		playerSystem.healthChangedEvent.register(event -> {
			healthbar.setValue(event.newHealth);
			healthbar.setMaxValue(event.newMaxHealth);
			healthbar.setBlinking(event.isInvulnerable);
		});
		addElement(healthbar, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_BOTTOM, 0, 10);

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

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/sprites/spider/color.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(2);
					mapBuilder.spiderButton();
				}
		));

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/sprites/spider/color.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(3);
					mapBuilder.spiderBossButton();
				}
		));

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/sprites/fireball/color.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(4);
					mapBuilder.turretButton();
				}
		));

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/sprites/fireball/color.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(5);
					mapBuilder.spikeTrapButton();
				}
		));

		buildPanelButtons.add(new GImageButton(
				Texture2DLoader.loadTexture("res/sprites/fireball/color.png"),
				Color.WHITE,
				BUTTON_SIZE,
				() -> {
					activateButton(6);
					mapBuilder.lavaButton();
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

		if (id >= 0) {
			buildPanelButtons.get(id).setPressed(true);
		}
	}

	public void setCurrentRoom(int roomId) {
		roundLabel.setText("Room: " + roomId);
	}

	private String formatTime(float time) {
		int seconds = (int) time;
		int minutes = seconds / 60;
		seconds -= minutes * 60;

		if (minutes > 0) {
			return String.format("Time: %d:%02d", minutes, seconds);
		} else {
			return String.format("Time: %02d", seconds);
		}

	}

	public void setTime(float time) {
		timeProgressbar.setValue((int) time);
		timeLabel.setText(formatTime(time));
	}

	@Override
	public void processInput(InputHandler inputHandler, float timeDelta) {
		super.processInput(inputHandler, timeDelta);

		for (MouseButtonEvent mouseButtonEvent : inputHandler.getMouseButtonEvents()) {
			if (mouseButtonEvent.action == GLFW.GLFW_PRESS && mouseButtonEvent.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				mapBuilder.noButton();
				activateButton(-1);
				mouseButtonEvent.setProcessed();
			}
		}

	}
}
