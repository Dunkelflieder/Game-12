package game12.core.components;

import game12.core.event.UpdateEvent;
import game12.core.map.Component;

public abstract class SimpleOnUpdateComponent extends Component {

	public abstract void update(UpdateEvent event);
}
