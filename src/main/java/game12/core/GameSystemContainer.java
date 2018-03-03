package game12.core;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import game12.core.systems.GameObjectsSystem;

public abstract class GameSystemContainer<T extends MapSystemContainer<?>> extends SystemContainer {

	private final T[] mapSystemContainers;

	public GameSystemContainer(EventManager eventManager, INetworkAdapter networkAdapter, T[] mapSystemContainers) {
		super(eventManager, networkAdapter);

		this.mapSystemContainers = mapSystemContainers;
	}

	public T[] getMapSystemContainers() { return mapSystemContainers; }

	@Override
	protected void addSystems() {
		addSystem(new GameObjectsSystem());
	}

	@Override
	public final String getName() {
		return "game";
	}

}
