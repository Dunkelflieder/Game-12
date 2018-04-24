package game12.client.controller.firstPerson;

import de.nerogar.noise.event.EventManager;
import game12.client.gui.GLabel;
import game12.client.gui.GProgressbar;
import game12.client.gui.Gui;
import game12.client.gui.GuiConstants;
import game12.client.map.ClientMap;
import game12.core.systems.PlayerSystem;

public class FirstPersonGui extends Gui {

	private final GProgressbar healthbar;

	private GLabel winLabel;

	public FirstPersonGui(EventManager eventManager, ClientMap map) {
		super(eventManager);
		PlayerSystem playerSystem = map.getSystem(PlayerSystem.class);

		healthbar = new GProgressbar(500, 20, playerSystem.getPlayerHealth().maxHealth, (value, maxValue) -> value + "/" + maxValue);
		playerSystem.healthChangedEvent.register(event -> {
			healthbar.setValue(event.newHealth);
			healthbar.setMaxValue(event.newMaxHealth);
			healthbar.setBlinking(event.isInvulnerable);
		});
		addElement(healthbar, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_BOTTOM, 0, 10);
	}

	public void addWinLabel(String message) {
		if (winLabel != null) removeElement(winLabel);
		winLabel = new GLabel(GuiConstants.DEFAULT_FONT, GuiConstants.FONT_COLOR, message);
		addElement(winLabel, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_CENTER, 0, 0);
	}

}
