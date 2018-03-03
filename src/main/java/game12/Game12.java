package game12;

import de.nerogar.noise.util.Logger;

public class Game12 {

	public static final boolean DEBUG = Boolean.getBoolean("game12.debug");

	public static final Logger logger = new Logger("Game12");
	public static final Logger renderLogger;

	public static final String DATA_DIR     = "data/";
	public static       String PACKAGE_NAME = "game12";

	public static final int NETWORK_ADAPTER_DEFAULT    = 1;
	public static final int NETWORK_ADAPTER_START_MAPS = 10;

	static {
		renderLogger = new Logger("Render");
		renderLogger.setParent(logger);
		renderLogger.setActive(false);
	}

}
