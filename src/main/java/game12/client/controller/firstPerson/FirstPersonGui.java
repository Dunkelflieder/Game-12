package game12.client.controller.firstPerson;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.event.EventManager;
import game12.client.gui.GHealthbar;
import game12.client.gui.Gui;
import game12.client.map.ClientMap;
import game12.core.networkEvents.HealthChangedEvent;
import game12.core.systems.PlayerSystem;

public class FirstPersonGui extends Gui {

	private final PlayerSystem playerSystem;

	private final EventListener<HealthChangedEvent> onPlayerHealthChanged = this::onPlayerHealthChanged;
	private final GHealthbar healthbar;

	public FirstPersonGui(EventManager eventManager, ClientMap map) {
		super(eventManager);
		playerSystem = map.getSystem(PlayerSystem.class);

		healthbar = new GHealthbar(500, 20, playerSystem.getPlayerHealth().maxHealth);
		playerSystem.getPlayerHealthChangedEvent().register(onPlayerHealthChanged);
		addElement(healthbar, Gui.ALIGNMENT_CENTER, Gui.ALIGNMENT_BOTTOM, 0, 10);
	}

	private void onPlayerHealthChanged(HealthChangedEvent event) {
		healthbar.setHealth(event.newHealth);
		healthbar.setMaxHealth(event.newMaxHealth);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		playerSystem.getPlayerHealthChangedEvent().unregister(onPlayerHealthChanged);
	}
}
