package game12.client.systems;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.render.Camera;
import de.nerogar.noise.render.PerspectiveCamera;
import de.nerogar.noise.render.deferredRenderer.DeferredRenderer;
import game12.ClientMain;
import game12.client.event.RenderEvent;
import game12.client.event.WindowSizeChangeEvent;
import game12.client.map.ClientMap;
import game12.core.LogicSystem;

public class RenderSystem extends LogicSystem {

	private ClientMap        map;
	private DeferredRenderer renderer;

	private EventListener<WindowSizeChangeEvent> windowSizeChangeListener;
	private EventListener<RenderEvent>           renderEventListener;
	private Camera                               camera;

	public RenderSystem(ClientMap map) {
		this.map = map;
	}

	@Override
	public void init() {
		windowSizeChangeListener = this::windowSizeChangeListenerFunction;
		getEventManager().register(WindowSizeChangeEvent.class, windowSizeChangeListener);

		renderEventListener = this::renderEventListenerFunction;
		getEventManager().registerImmediate(RenderEvent.class, renderEventListener);

		renderer = new DeferredRenderer(ClientMain.window.getWidth(), ClientMain.window.getHeight());
		renderer.setSunLightBrightness(1.0f);

		camera = new PerspectiveCamera(90, (float) ClientMain.window.getWidth() / ClientMain.window.getHeight(), 0.1f, 1000f);
	}

	public ClientMap getMap()             { return map; }

	public DeferredRenderer getRenderer() { return renderer; }

	public Camera getCamera()             { return camera; }

	public void setCamera(Camera camera)  { this.camera = camera; }

	private void windowSizeChangeListenerFunction(WindowSizeChangeEvent event) {
		renderer.setFrameBufferResolution(event.getWidth(), event.getHeight());

		camera.setAspect(event.getAspect());
	}

	private void renderEventListenerFunction(RenderEvent event) {

		renderer.render(camera);
	}

	@Override
	public void cleanup() {
		getEventManager().unregister(WindowSizeChangeEvent.class, windowSizeChangeListener);
		getEventManager().unregister(RenderEvent.class, renderEventListener);

		renderer.cleanup();
	}

}
