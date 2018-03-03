package game12.client;

import de.nerogar.noise.event.EventManager;
import game12.core.Faction;
import game12.core.FactionSystemContainer;
import game12.core.Side;

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
