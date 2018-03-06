package game12.core.systems;

import de.nerogar.noise.util.Vector3f;
import game12.client.components.SpriteComponent;
import game12.core.EntityFactorySystem;
import game12.core.Side;
import game12.core.SynchronizedSystem;
import game12.core.components.DoorComponent;
import game12.core.components.PlayerComponent;
import game12.core.components.PositionComponent;
import game12.core.event.MapChangeEvent;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;
import game12.core.map.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class DoorSystem extends SynchronizedSystem {

	private static final float DOOR_OPEN_RADIUS = 1.5f;

	private Entity[] doors;

	private CoreMap   map;
	private MapSystem mapSystem;
	private short     doorEntityId;

	public DoorSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		mapSystem = getContainer().getSystem(MapSystem.class);

		doors = new Entity[mapSystem.getWidth() * mapSystem.getWidth()];

		doorEntityId = map.getGameSystem(GameObjectsSystem.class).getID("door");

		if (checkSide(Side.CLIENT)) {
			getEventManager().register(UpdateEvent.class, this::updateVisual);
		}

		if (checkSide(Side.SERVER)) {
			getEventManager().register(MapChangeEvent.class, this::onMapChange);
		}
	}

	private void onMapChange(MapChangeEvent event) {
		int index = event.getY() * mapSystem.getWidth() + event.getX();

		if (doors[index] != null) {
			map.getEntityList().remove(doors[index].getID());
		}

		if (event.getNewRoom() == MapSystem.DOOR || event.getNewRoom() == MapSystem.LOCKED_DOOR) {
			Entity door = map.getSystem(EntityFactorySystem.class).createEntity(doorEntityId, event.getX() + 0.5f, 0.0f, event.getY() + 0.5f);
			DoorComponent doorComponent = door.getComponent(DoorComponent.class);

			Vector3f openDirection = new Vector3f();
			if (mapSystem.get(event.getX() + 1, event.getY()) == MapSystem.VOID && mapSystem.get(event.getX() - 1, event.getY()) == MapSystem.VOID) {
				openDirection.setX(1);
			} else {
				openDirection.setZ(1);
			}

			doorComponent.setInitialState(
					new Vector3f(event.getX() + 0.5f, 0.0f, event.getY() + 0.5f),
					openDirection,
					event.getNewRoom() == MapSystem.LOCKED_DOOR,
					0
			                             );

			doors[index] = door;
		}
	}

	private void updateVisual(UpdateEvent event) {

		Vector3f playerPosition = getPlayerPosition();
		Vector3f temp = new Vector3f();
		for (DoorComponent doorComponent : map.getEntityList().getComponents(DoorComponent.class)) {

			temp.set(doorComponent.getPosition());
			temp.subtract(playerPosition);

			if (temp.getValue() <= DOOR_OPEN_RADIUS && !doorComponent.isLocked()) {
				doorComponent.setTargetState(true);
			} else {
				doorComponent.setTargetState(false);
			}

			doorComponent.update(event.getDelta());

			SpriteComponent spriteComponent = doorComponent.getEntity().getComponent(SpriteComponent.class);
			if (doorComponent.getOpenDirection().getX() > 0) {
				spriteComponent.setForcedRotation(new Vector3f(0.0f, 0.0f, 0.0f));
			} else {
				spriteComponent.setForcedRotation(new Vector3f((float) (Math.PI * 0.5f), 0.0f, 0.0f));
			}
		}
	}

	private Vector3f getPlayerPosition() {
		Iterator<PlayerComponent> iterator = map.getEntityList().getComponents(PlayerComponent.class).iterator();
		if (!iterator.hasNext()) return new Vector3f();
		PositionComponent positionComponent = iterator.next().getEntity().getComponent(PositionComponent.class);
		return new Vector3f(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {

	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {

	}
}
