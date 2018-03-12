package game12.core.systems;

import de.nerogar.noise.util.Vector3f;
import game12.core.components.HealthComponent;
import game12.core.components.PlayerComponent;
import game12.core.components.PositionComponent;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;
import game12.core.map.Entity;
import game12.core.networkEvents.HealthChangedEvent;
import game12.core.utils.EventContainer;

import java.util.Iterator;

public class PlayerSystem extends OnUpdateSystem {

	private CoreMap           map;
	private Entity            player;
	private PositionComponent playerPosition;

	private MapSystem mapSystem;

	public PlayerSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		super.init();
		mapSystem = getContainer().getSystem(MapSystem.class);
		getEventManager().register(HealthChangedEvent.class, event -> {
			Entity entity = map.getEntity(event.entityID);
			if (entity != null && entity.isValid() && entity.hasComponent(PlayerComponent.class)) {
				playerHealthChangedEvent.trigger(event);
			}
		});
	}

	@Override
	protected void updateListenerFunction(UpdateEvent event) {
		Iterator<PlayerComponent> iter = map.getEntityList().getComponents(PlayerComponent.class).iterator();
		if (iter.hasNext()) {
			player = iter.next().getEntity();
			playerPosition = player.getComponent(PositionComponent.class);
		} else {
			player = null;
			playerPosition = null;
		}
	}

	public Vector3f getPlayerPosition() {
		if (playerPosition == null) {
			return new Vector3f();
		} else {
			return new Vector3f(playerPosition.getX(), playerPosition.getY(), playerPosition.getZ());
		}
	}

	public void setPlayerPosition(Vector3f position) {
		if (playerPosition != null) {
			playerPosition.setPosition(position.getX(), position.getY(), position.getZ());
		}
	}

	public int getPlayerRoomID() {
		if (player == null) {
			return -1;
		} else {
			return mapSystem.get((int) playerPosition.getX(), (int) playerPosition.getZ());
		}
	}

	public HealthComponent getPlayerHealth() {
		if (player == null) {
			return new HealthComponent(1, 0);
		} else {
			return player.getComponent(HealthComponent.class);
		}
	}

	private final EventContainer<HealthChangedEvent> playerHealthChangedEvent = new EventContainer<>();

	public EventContainer<HealthChangedEvent> getPlayerHealthChangedEvent() {
		return playerHealthChangedEvent;
	}
}
