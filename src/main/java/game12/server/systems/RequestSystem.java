package game12.server.systems;

import de.nerogar.noise.event.EventListener;
import game12.core.LogicSystem;
import game12.core.network.FactionRequestPacket;

public abstract class RequestSystem<T extends FactionRequestPacket> extends LogicSystem {

	private EventListener<T> requestListener;
	private Class<T>         requestClass;

	public RequestSystem(Class<T> requestClass) {
		this.requestClass = requestClass;
	}

	@Override
	public void init() {
		requestListener = this::requestFunction;
		getEventManager().register(requestClass, requestListener);
	}

	protected abstract void requestFunction(T request);

	@Override
	public void cleanup() {
		super.cleanup();

		getEventManager().unregister(requestClass, requestListener);
	}
}
