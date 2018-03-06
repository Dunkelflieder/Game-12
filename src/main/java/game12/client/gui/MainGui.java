package game12.client.gui;

import de.nerogar.noise.event.EventListener;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.render.Shader;
import de.nerogar.noise.render.ShaderLoader;
import de.nerogar.noise.render.fontRenderer.Font;
import de.nerogar.noise.util.Color;
import game12.client.Client;
import game12.client.Menu;
import game12.client.event.BeforeRenderEvent;

public class MainGui extends Gui {

	private Menu menu;

	private final Shader backgroundShader;
	private final GPanel backgroundPanel;

	private GPanel mainPanel;
	private GPanel testPanel;

	private EventListener<BeforeRenderEvent> beforeRenderEventListener;
	private float                            time;

	private final int PANEL_SIZE    = 800;
	private final int BUTTON_OFFSET = 50;
	private final int BUTTON_SIZE   = PANEL_SIZE - BUTTON_OFFSET;

	private float backgroundBlendOut = 0;

	private float mainBlendOut = 0;
	private float mainBlendDirection;

	private float testBlendOut = 2;
	private float testBlendDirection;

	private float panelBlendOut = 0;
	private Runnable afterBlendOutAction;

	public MainGui(Menu menu, EventManager eventManager) {
		super(eventManager);
		this.menu = menu;

		this.beforeRenderEventListener = this::beforeRenderEventListenerFunction;
		getEventManager().register(BeforeRenderEvent.class, beforeRenderEventListener);

		// font
		Font font = new Font("Consolas", 40);
		Color fontColor = new Color(0.7f, 0.7f, 0.7f, 1.0f);
		Color fontHoverColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

		// background
		backgroundShader = ShaderLoader.loadShader("shaders/gui/background/background.vert", "shaders/gui/background/background.frag");
		backgroundPanel = new GPanel(new Color(0.0f, 0.0f, 0.0f, 1.0f), backgroundShader, 0, 0);
		addElement(backgroundPanel, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, 0, 0);

		createMainPanel(font, fontColor, fontHoverColor);
		createTestPanel(font, fontColor, fontHoverColor);

	}

	private void createMainPanel(Font font, Color fontColor, Color fontHoverColor) {
		mainPanel = new GPanel(new Color(0.3f, 0.3f, 0.3f, 0.0f), PANEL_SIZE, 0);
		backgroundPanel.addElement(mainPanel, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, 0, 0);

		GButton localButtonFP = new GButton(font, "start local game (first person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.startLocalGame(Client.ControllerType.FIRST_PERSON)));
		localButtonFP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(localButtonFP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 50);

		GButton localButtonTP = new GButton(font, "start local game (third person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.startLocalGame(Client.ControllerType.THIRD_PERSON)));
		localButtonTP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(localButtonTP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 100);

		GButton startServerButtonFP = new GButton(font, "start server (first person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.startServer(Client.ControllerType.FIRST_PERSON)));
		startServerButtonFP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(startServerButtonFP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 200);

		GButton startServerButtonTP = new GButton(font, "start server (third person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.startServer(Client.ControllerType.THIRD_PERSON)));
		startServerButtonTP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(startServerButtonTP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 250);

		GButton connectStartButtonFP = new GButton(font, "connect and start (first person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.connectStartServer(Client.ControllerType.FIRST_PERSON)));
		connectStartButtonFP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(connectStartButtonFP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 300);

		GButton connectStartButtonTP = new GButton(font, "connect and start (third person)", BUTTON_SIZE, 40, () -> blendPanelOut(() -> menu.connectStartServer(Client.ControllerType.THIRD_PERSON)));
		connectStartButtonTP.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(connectStartButtonTP, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 350);

/*
		GButton blendOutButton = new GButton(font, "blend out", BUTTON_SIZE, 40, () -> backgroundBlendOut = 0.001f);
		blendOutButton.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(blendOutButton, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 450);

		GButton resetBlendOutButton = new GButton(font, "reset blend out", BUTTON_SIZE, 40, () -> backgroundBlendOut = 0.0f);
		resetBlendOutButton.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(resetBlendOutButton, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 500);
*/

		GButton testPanelButton = new GButton(font, "switch panels", BUTTON_SIZE, 40, () -> {
			mainBlendDirection = 10;
			testBlendDirection = -10;
		});
		testPanelButton.setColors(fontColor, fontHoverColor);
		mainPanel.addElement(testPanelButton, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 600);
	}

	private void createTestPanel(Font font, Color fontColor, Color fontHoverColor) {
		testPanel = new GPanel(new Color(0.3f, 0.3f, 0.3f, 0.0f), PANEL_SIZE, 10000);
		backgroundPanel.addElement(testPanel, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, 0, 0);

		GButton button1 = new GButton(font, "test Button 1", BUTTON_SIZE, 40, () -> {});
		button1.setColors(fontColor, fontHoverColor);
		testPanel.addElement(button1, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 50);

		GButton backButton = new GButton(font, "back", BUTTON_SIZE, 40, () -> {
			mainBlendDirection = -10;
			testBlendDirection = 10;
		});
		backButton.setColors(fontColor, fontHoverColor);
		testPanel.addElement(backButton, GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, BUTTON_OFFSET, 100);
	}

	private void blendPanelOut(Runnable action) {
		mainBlendDirection = 10;
		testBlendDirection = 10;
		panelBlendOut = 0.0001f;

		afterBlendOutAction = action;
	}

	@Override
	public void startBlendOut() {
		backgroundBlendOut = 0.0001f;
	}

	@Override
	public boolean isBlendOutDone() {
		return backgroundBlendOut > width + height;
	}

	private void beforeRenderEventListenerFunction(BeforeRenderEvent event) {
		time += event.getDelta();

		if (backgroundBlendOut != 0) {
			backgroundBlendOut += event.getDelta() * 1400.0f;
		}

		if (panelBlendOut != 0) {
			panelBlendOut += event.getDelta() * 2.0f;
			panelBlendOut = Math.min(1.0f, panelBlendOut);
		}

		mainBlendOut += mainBlendDirection * event.getDelta();
		mainBlendOut = Math.max(0, mainBlendOut);
		mainBlendOut = Math.min(2, mainBlendOut);

		testBlendOut += testBlendDirection * event.getDelta();
		testBlendOut = Math.max(0, testBlendOut);
		testBlendOut = Math.min(2, testBlendOut);

		backgroundPanel.setSize(width, height);
		mainPanel.setPosition(GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, (int) (-PANEL_SIZE * mainBlendOut), 0);
		mainPanel.setSize(PANEL_SIZE, height);
		testPanel.setPosition(GElement.ALIGNMENT_LEFT, GElement.ALIGNMENT_TOP, (int) (-PANEL_SIZE * testBlendOut), 0);
		testPanel.setSize(PANEL_SIZE, height);
		recalculatePosition(posX, posY, width, height);

		backgroundShader.activate();
		backgroundShader.setUniform1f("time", time);
		backgroundShader.setUniform1f("panelWidth", 500);
		backgroundShader.setUniform1f("panelBlendOut", panelBlendOut);
		backgroundShader.setUniform1f("backgroundBlendOut", backgroundBlendOut);
		backgroundShader.deactivate();

		if (panelBlendOut == 1) {
			if (afterBlendOutAction != null) {
				afterBlendOutAction.run();
				afterBlendOutAction = null;
			}
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		getEventManager().unregister(BeforeRenderEvent.class, beforeRenderEventListener);
	}

}
