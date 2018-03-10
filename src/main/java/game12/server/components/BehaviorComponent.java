package game12.server.components;

import game12.core.map.Component;

public abstract class BehaviorComponent extends Component {

	private int ownRoom;

	public int getOwnRoom() {
		return ownRoom;
	}

	public void setOwnRoom(int ownRoom) {
		this.ownRoom = ownRoom;
	}
}
