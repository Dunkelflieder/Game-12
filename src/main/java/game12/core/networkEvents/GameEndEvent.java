package game12.core.networkEvents;

import game12.core.network.NetworkEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GameEndEvent extends NetworkEvent {

	public static final int PLAYER_NONE  = 0;
	public static final int PLAYER_FIRST = 1;
	public static final int PLAYER_THIRD = 2;

	public int winner;

	public GameEndEvent() {
	}

	public GameEndEvent(int winner) {
		this.winner = winner;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		winner = in.readInt();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(winner);
	}
}
