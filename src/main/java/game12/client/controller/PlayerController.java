package game12.client.controller;

import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.network.INetworkAdapter;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.PerspectiveCamera;
import de.nerogar.noise.util.Color;
import game12.client.Controller;
import game12.client.gui.GLabel;
import game12.client.gui.Gui;
import game12.client.gui.GuiConstants;
import game12.client.gui.GuiContainer;
import game12.client.map.ClientMap;
import game12.client.systems.RenderSystem;

import java.util.List;

public class PlayerController extends Controller {

	private final PerspectiveCamera cam;

	public PlayerController(GLWindow window, EventManager eventManager, List<ClientMap> maps, INetworkAdapter networkAdapter,
			GuiContainer guiContainer) {
		super(window, eventManager, maps, networkAdapter, guiContainer);

		Gui gui = new Gui(eventManager);
		GLabel label = new GLabel(GuiConstants.DEFAULT_FONT, Color.CYAN, "Lorem ipsum dolor sit amet.");
		gui.addElement(label, Gui.ALIGNMENT_LEFT, Gui.ALIGNMENT_TOP, 0, 0);
		this.guiContainer.setActiveGui(gui);

		maps.get(0).setActive(true);

		cam = maps.get(0).getSystem(RenderSystem.class).getCamera();
	}

	@Override
	public void update(float timeDelta) {
		cam.setXYZ(2, 2, 5);
		cam.setLookAt(0, 0, 0);
		cam.setFOV(90);
	}


}
