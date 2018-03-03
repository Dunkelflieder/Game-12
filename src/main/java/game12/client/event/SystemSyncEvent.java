package game12.client.event;

import de.nerogar.noise.event.Event;
import game12.core.SystemSyncParameter;

public class SystemSyncEvent implements Event {

	private SystemSyncParameter syncParameter;

	public SystemSyncEvent(SystemSyncParameter syncParameter) {
		this.syncParameter = syncParameter;
	}

	public SystemSyncParameter getSyncParameter() {
		return syncParameter;
	}
}
