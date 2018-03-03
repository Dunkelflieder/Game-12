package strategy.client;

import de.nerogar.noise.event.EventManager;
import strategy.core.Faction;
import strategy.core.FactionSystemContainer;
import strategy.core.Side;

public class ClientFactionSystemContainer extends FactionSystemContainer {

	public ClientFactionSystemContainer(EventManager eventManager, Faction faction) {
		super(eventManager, faction, faction);
	}

	@Override
	protected void addSystems() {
		super.addSystems();
	}

	@Override
	public Side getSide() { return Side.CLIENT; }

}
