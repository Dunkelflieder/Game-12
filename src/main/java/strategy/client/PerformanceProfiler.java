package strategy.client;

import de.nerogar.noise.debug.Profiler;
import de.nerogar.noise.util.Color;

public class PerformanceProfiler extends Profiler {

	private static final int TIME      = 0;
	private static final int FREQUENCY = 1;

	public static final int TIME_CALC  = 0;
	public static final int TIME_FRAME = 1;

	public static final int FREQUENCY_FRAME = 2;

	public PerformanceProfiler() {
		super("performance", true);

		registerProperty(TIME_CALC, TIME, new Color(0.8f, 0.0f, 0.1f, 1.0f), "calc time");
		registerProperty(TIME_FRAME, TIME, new Color(0.8f, 0.4f, 0.2f, 1.0f), "frame time");

		registerProperty(FREQUENCY_FRAME, FREQUENCY, new Color(0.3f, 0.8f, 0.2f, 1.0f), "fps");
	}

}
