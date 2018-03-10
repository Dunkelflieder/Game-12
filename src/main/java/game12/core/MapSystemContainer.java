package game12.core;

import game12.core.map.CoreMap;
import game12.core.systems.*;
import game12.server.systems.PositionLookupSystem;

public abstract class MapSystemContainer<T extends CoreMap> extends SystemContainer {

	private T map;

	public MapSystemContainer(T map) {
		super(map.getEventManager(), map.getNetworkAdapter());

		this.map = map;
	}

	public T getMap() {
		if (map == null) throw new NullPointerException();
		return map;
	}

	@Override
	protected void addSystems() {
		addSystem(new PositionLookupSystem());

		addSystem(new MapSystem(100, 100));
		addSystem(new DoorSystem(getMap()));
		addSystem(new GameProgressSystem(getMap()));
		addSystem(new LifetimeSystem(getMap()));
		addSystem(new PlayerSystem(getMap()));
	}

	@Override
	public final String getName() {
		return "map " + map.getId();
	}

}
