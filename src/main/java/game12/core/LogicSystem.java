package game12.core;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.serialization.NDSNodeObject;

public abstract class LogicSystem {

	private SystemContainer container;

	private INetworkAdapter networkAdapter;
	private EventManager    eventManager;

	protected void setObjects(SystemContainer container, INetworkAdapter networkAdapter, EventManager eventManager) {
		this.container = container;
		this.networkAdapter = networkAdapter;
		this.eventManager = eventManager;
	}

	protected boolean checkSide(Side side)              { return side == getContainer().getSide(); }

	public SystemContainer getContainer()               { return container; }

	public INetworkAdapter getNetworkAdapter()          { return networkAdapter; }

	public EventManager getEventManager()               { return eventManager; }

	public void init()                                  { }

	public void setSystemData(NDSNodeObject systemData) { }

	public void initWithData()                          { }

	public void cleanup()                               { }

}
