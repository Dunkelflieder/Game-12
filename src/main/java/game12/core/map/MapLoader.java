package game12.core.map;

import game12.core.Faction;

import java.util.List;

public abstract class MapLoader<T extends CoreMap> extends Thread {

	private boolean done;

	private Faction[] factions;

	public MapLoader(List<T> maps, String mapID, Faction[] factions) {
		super("map loader: " + mapID);
		this.factions = factions;
	}

	protected abstract T newMap(int id, Faction[] factions);

	public void loadMeta() {

	}

	public void startLoading() {
		start();
	}

	@Override
	public void run() {
		done = true;
	}

	public boolean isDone() {
		return done;
	}

	public void finalizeLoad() {

	}

}
