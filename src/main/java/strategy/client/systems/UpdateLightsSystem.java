package strategy.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import strategy.client.components.LightComponent;
import strategy.client.map.ClientMap;
import strategy.core.LogicSystem;
import strategy.core.event.EntityMoveEvent;

public class UpdateLightsSystem extends LogicSystem {

	private ClientMap map;

	private DeferredRenderer renderer;

	private EventListener<EntityMoveEvent> moveListener;

	public UpdateLightsSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		moveListener = this::moveListenerFunction;
		getEventManager().register(EntityMoveEvent.class, moveListener);

		this.renderer = map.getSystem(RenderSystem.class).getRenderer();
	}

	public void registerEntity(LightComponent component) {
		renderer.getLightContainer().add(component.getLight());
	}

	private void moveListenerFunction(EntityMoveEvent event) {
		LightComponent lightComponent = event.getEntity().getComponent(LightComponent.class);
		if (lightComponent != null) {
			lightComponent.setPosition();
		}
	}

	public void unregisterEntity(LightComponent component) {
		renderer.getLightContainer().remove(component.getLight());
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(EntityMoveEvent.class, moveListener);
	}

}
