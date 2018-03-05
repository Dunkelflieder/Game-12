package game12.core.systems;

import game12.core.EventTimer;
import game12.core.Side;
import game12.core.SynchronizedSystem;
import game12.core.SystemSyncParameter;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameProgressSystem extends SynchronizedSystem {

	private CoreMap map;

	private int currentRoom = 2;
	private EventTimer nextRoomTimer;

	private MapSystem mapSystem;

	public GameProgressSystem(CoreMap map) {
		this.map = map;

		nextRoomTimer = new EventTimer(20, false, -1);
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = getContainer().getSystem(MapSystem.class);

		registerSyncFunction(NextRoomSyncParameter.class, this::syncNextRoom);

		if (checkSide(Side.SERVER)) {
			getEventManager().register(UpdateEvent.class, this::onUpdate);
		}
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {

	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {

	}

	public int getCurrentRoom() {
		return currentRoom;
	}

	private void syncNextRoom(NextRoomSyncParameter parameter) {
		mapSystem.lockRoom(currentRoom);
		currentRoom++;
	}

	private void onUpdate(UpdateEvent event) {
		nextRoomTimer.update(event.getDelta());

		while (nextRoomTimer.trigger()) {
			mapSystem.lockRoom(currentRoom);
			currentRoom++;
			callSyncFunction(new NextRoomSyncParameter());
		}

	}

	public static class NextRoomSyncParameter extends SystemSyncParameter {

	}

}
