package game12.core.components;

import de.nerogar.noise.network.Streamable;
import game12.core.Side;
import game12.core.map.Component;
import game12.server.event.ComponentUpdateEvent;

public abstract class SynchronizedComponent extends Component implements Streamable {

	protected void synchronize() {
		if (getEntity().getMap().getSide() == Side.SERVER) {
			getEntity().getMap().getEventManager().trigger(new ComponentUpdateEvent(this));
		}
	}

}
