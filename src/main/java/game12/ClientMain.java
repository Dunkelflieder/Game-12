package game12;

import de.nerogar.noise.Noise;
import de.nerogar.noise.event.EventManager;
import de.nerogar.noise.opencl.CLContext;
import de.nerogar.noise.opencl.CLException;
import de.nerogar.noise.render.GLWindow;
import de.nerogar.noise.render.Monitor;
import de.nerogar.noise.util.Logger;
import de.nerogar.noise.util.Timer;
import game12.client.Client;
import game12.client.Menu;
import game12.client.PerformanceProfiler;
import game12.client.event.BeforeRenderEvent;
import game12.client.event.ClientChangeEvent;
import game12.client.event.WindowSizeChangeEvent;
import game12.client.gui.GuiConstants;
import game12.client.gui.GuiContainer;
import game12.core.Components;

public class ClientMain {

	public static  GLWindow     window;
	public static  CLContext    clContext;
	private static EventManager mainEventManager;
	private static GuiContainer guiContainer;

	private static Menu   menu;
	private static Client client;

	private static PerformanceProfiler performanceProfiler;

	private static void initLib() {
		// debug initialization
		//System.setProperty("org.lwjgl.util.Debug", "true");

		// logger
		Game12.logger.setParent(Noise.getLogger());

		// noise
		Noise.init("noiseSettings.json");

		// game
		Components.init();

	}

	private static void init() {
		performanceProfiler = new PerformanceProfiler();
		Noise.getDebugWindow().addProfiler(performanceProfiler);

		window = new GLWindow("", 1280, 720, true, 0, null, null);
		window.setSizeChangeListener(ClientMain::windowSizeChanged);

		GuiConstants.init();

		try {
			clContext = new CLContext(window.getGLContext());
		} catch (CLException exception) {
			Game12.logger.log(Logger.WARNING, "Could not create openCL context");
		}

		mainEventManager = new EventManager("ClientWindow");

		guiContainer = new GuiContainer(window.getWidth(), window.getHeight());

		menu = new Menu(window, guiContainer, mainEventManager);
	}

	private static void windowSizeChanged(int width, int height) {
		mainEventManager.trigger(new WindowSizeChangeEvent(width, height));
		guiContainer.setFrameBufferResolution(width, height);
	}

	public static void setClient(Client newClient) {
		client = newClient;
		mainEventManager.trigger(new ClientChangeEvent(client));
	}

	private static void loop(float timeDelta) {
		// update menu
		menu.update(timeDelta);

		// update client
		if (client != null) {
			client.update(timeDelta);
			ClientMain.mainEventManager.trigger(new BeforeRenderEvent(timeDelta));
			client.render(timeDelta);
		} else {
			ClientMain.mainEventManager.trigger(new BeforeRenderEvent(timeDelta));
			menu.render();
		}

	}

	private static void run() {
		Timer timer = new Timer();

		while (!window.shouldClose()) {
			timer.update(1f / 60f); // TODO load refresh rate somehow
			loop(timer.getDelta());

			performanceProfiler.setValue(PerformanceProfiler.TIME_CALC, (int) (timer.getCalcTime() * 1_000_000));
			performanceProfiler.setValue(PerformanceProfiler.TIME_FRAME, (int) (timer.getDelta() * 1_000_000));
			performanceProfiler.setValue(PerformanceProfiler.FREQUENCY_FRAME, (int) (timer.getFrequency()));

			GLWindow.updateAll();

			if (timer.getCalcTime() > 0.1) Game12.logger.log(Logger.INFO, "Long frame time: " + timer.getCalcTime() + "s");
		}

	}

	public static void main(String[] args) {
		initLib();
		init();

		run();
	}

}
