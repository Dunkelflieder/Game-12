package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import game12.client.components.RenderComponent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;
import game12.core.event.EntityMoveEvent;

public class UpdateRenderablesSystem extends LogicSystem {

	private ClientMap        map;
	private DeferredRenderer renderer;

	private EventListener<EntityMoveEvent> moveListener;

	public UpdateRenderablesSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		moveListener = this::moveListenerFunction;
		getEventManager().registerImmediate(EntityMoveEvent.class, moveListener);

		this.renderer = map.getSystem(RenderSystem.class).getRenderer();
	}

	public void registerEntity(RenderComponent component) {
		renderer.addObject(component.getRenderable());
	}

	private void moveListenerFunction(EntityMoveEvent event) {
		RenderComponent renderComponent = event.getEntity().getComponent(RenderComponent.class);
		renderComponent.updatePosition(event.getNewX(), event.getNewY(), event.getNewZ(), event.getNewRotation(), event.getNewScale());
	}

	public void unregisterEntity(RenderComponent component) {
		renderer.removeObject(component.getRenderable());
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntityMoveEvent.class, moveListener);
	}

}
