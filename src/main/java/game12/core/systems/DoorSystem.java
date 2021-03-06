package game12.core.systems;

import de.nerogar.noise.util.Vector3f;
import game12.client.event.DoorCloseEvent;
import game12.client.event.DoorOpenEvent;
import game12.client.systems.SoundSystem;
import game12.core.EntityFactorySystem;
import game12.core.Side;
import game12.core.SynchronizedSystem;
import game12.core.components.DoorComponent;
import game12.core.components.PositionComponent;
import game12.core.event.MapChangeEvent;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;
import game12.core.map.Entity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoorSystem extends SynchronizedSystem {

	private static final float DOOR_OPEN_RADIUS = 1.5f;

	private Entity[] doors;

	private CoreMap      map;
	private MapSystem    mapSystem;
	private short        doorEntityId;
	private PlayerSystem playerSystem;

	public DoorSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		mapSystem = getContainer().getSystem(MapSystem.class);
		playerSystem = getContainer().getSystem(PlayerSystem.class);

		doors = new Entity[mapSystem.getWidth() * mapSystem.getWidth()];

		doorEntityId = map.getGameSystem(GameObjectsSystem.class).getID("door");

		if (checkSide(Side.CLIENT)) {
			getEventManager().register(UpdateEvent.class, this::updateVisual);
			getEventManager().register(DoorOpenEvent.class, this::onDoorOpen);
			getEventManager().register(DoorCloseEvent.class, this::onDoorClose);
		}

		if (checkSide(Side.SERVER)) {
			getEventManager().register(MapChangeEvent.class, this::onMapChange);
		}
	}

	private void onMapChange(MapChangeEvent event) {
		int index = event.getY() * mapSystem.getWidth() + event.getX();

		if (doors[index] != null) {
			map.getEntityList().remove(doors[index].getID());
			doors[index] = null;
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

		Vector3f playerPosition = playerSystem.getPlayerPosition();
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

			PositionComponent positionComponent = doorComponent.getEntity().getComponent(PositionComponent.class);
			if (doorComponent.getOpenDirection().getX() > 0) {
				positionComponent.setRotation(0);
			} else {
				positionComponent.setRotation((float) (Math.PI / 2f));
			}
		}
	}

	private void onDoorOpen(DoorOpenEvent event) {
		getContainer().getSystem(SoundSystem.class).playSound("res/sound/door/open.ogg", event.position.getX(), event.position.getY(), event.position.getZ());
	}

	private void onDoorClose(DoorCloseEvent event) {
		getContainer().getSystem(SoundSystem.class).playSound("res/sound/door/close.ogg", event.position.getX(), event.position.getY(), event.position.getZ());
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {

	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {

	}
}
