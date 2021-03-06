package game12.core.systems;

import game12.core.Side;
import game12.core.SynchronizedSystem;
import game12.core.SystemSyncParameter;
import game12.core.event.UpdateEvent;
import game12.core.map.CoreMap;
import game12.core.networkEvents.GameEndEvent;
import game12.core.networkEvents.HealthChangedEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameProgressSystem extends SynchronizedSystem {

	public static final float ROOM_TIME      = 120;
	public static final float OVERTIME       = 30;
	public static final int   OVERTIME_COUNT = 2;

	private CoreMap map;

	private int   currentRoom;
	private float time;
	private int   state; // 0 = room build tim, >0 = overtime

	private boolean hasGameEnded;

	private MapSystem    mapSystem;
	private PlayerSystem playerSystem;

	public GameProgressSystem(CoreMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		super.init();

		this.mapSystem = getContainer().getSystem(MapSystem.class);
		this.playerSystem = getContainer().getSystem(PlayerSystem.class);

		time = ROOM_TIME;
		currentRoom = 2;

		registerSyncFunction(StateSyncParameter.class, this::syncNextRoom);

		getEventManager().register(UpdateEvent.class, this::onUpdate);
		playerSystem.healthChangedEvent.register(this::checkFirstPersonPlayer);
	}

	@Override
	public void sendNetworkInit(DataOutputStream out) throws IOException {

	}

	@Override
	public void networkInit(DataInputStream in) throws IOException {

	}

	public int getCurrentRoom() { return currentRoom; }

	public float getTime()      { return time; }

	public int getState()       { return state; }

	private void syncNextRoom(StateSyncParameter parameter) {
		nextState(parameter.state, parameter.time, parameter.currentRoom);
	}

	private void onUpdate(UpdateEvent event) {
		time -= event.getDelta();
		if (!checkSide(Side.SERVER)) return;

		if (time <= 0) {
			boolean isValid = mapSystem.checkRoom(currentRoom);

			if (isValid) {
				nextState(0, ROOM_TIME, currentRoom + 1);
				callSyncFunction(new StateSyncParameter(state, time, currentRoom));
			} else {
				if (state < OVERTIME_COUNT) {
					nextState(state + 1, OVERTIME, currentRoom);
					callSyncFunction(new StateSyncParameter(state, time, currentRoom));
				} else {
					if (!hasGameEnded) {
						getNetworkAdapter().send(new GameEndEvent(GameEndEvent.PLAYER_FIRST));
						hasGameEnded = true;
					}
				}
			}
		}

	}

	private void checkFirstPersonPlayer(HealthChangedEvent event) {
		if (event.newHealth <= 0) {
			if (!hasGameEnded) {
				getNetworkAdapter().send(new GameEndEvent(GameEndEvent.PLAYER_THIRD));
				hasGameEnded = true;
			}
		}
	}

	private void nextState(int state, float time, int currentRoom) {
		this.state = state;
		this.time = time;
		this.currentRoom = currentRoom;
	}

	public static class StateSyncParameter extends SystemSyncParameter {

		private int   state;
		private float time;
		private int   currentRoom;

		public StateSyncParameter() {
		}

		public StateSyncParameter(int state, float time, int currentRoom) {
			this.state = state;
			this.time = time;
			this.currentRoom = currentRoom;
		}

		@Override
		public void fromStream(DataInputStream in) throws IOException {
			super.fromStream(in);

			state = in.readInt();
			time = in.readFloat();
			currentRoom = in.readInt();
		}

		@Override
		public void toStream(DataOutputStream out) throws IOException {
			super.toStream(out);

			out.writeInt(state);
			out.writeFloat(time);
			out.writeInt(currentRoom);
		}

	}

}
