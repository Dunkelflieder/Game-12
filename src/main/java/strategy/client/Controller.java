package strategy.client;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.input.InputHandler;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import strategy.client.gui.GuiContainer;
import strategy.client.map.ClientMap;

import java.util.List;

public abstract class Controller {

	protected GLWindow        window;
	protected InputHandler    inputHandler;
	protected EventManager    eventManager;
	protected List<ClientMap> maps;
	protected INetworkAdapter networkAdapter;
	protected GuiContainer    guiContainer;

	public Controller(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter, GuiContainer guiContainer) {
		this.window = window;
		this.inputHandler = window.getInputHandler();
		this.eventManager = eventManager;
		this.maps = maps;
		this.networkAdapter = networkAdapter;
		this.guiContainer = guiContainer;
	}

	public abstract void update(float timeDelta);

}
