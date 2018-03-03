package strategy.core.components;

import de.nerogar.noise.network.Streamable;
import strategy.core.Side;
import strategy.core.map.Component;
import strategy.server.event.ComponentUpdateEvent;

public abstract class SynchronizedComponent extends Component implements Streamable {

	protected void synchronize() {
		if (getEntity().getMap().getSide() == Side.SERVER) {
			getEntity().getMap().getEventManager().trigger(new ComponentUpdateEvent(this));
		}
	}

}
