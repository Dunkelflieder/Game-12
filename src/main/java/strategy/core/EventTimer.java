package strategy.core;

public class EventTimer {

	private float delay;
	private float lastTrigger;
	private int   maxExecutions;

	private int triggerAmount;

	/**
	 * @param delay      the delay between executions
	 * @param randomInit true, if the initial delay should be random (between 0 and {@code delay})
	 */
	public EventTimer(float delay, boolean randomInit) {
		this(delay, randomInit, -1);
	}

	/**
	 * @param delay         the delay between executions
	 * @param randomInit    true, if the initial delay should be random (between 0 and {@code delay})
	 * @param maxExecutions max number of executions, or -1 for infinite executions
	 */
	public EventTimer(float delay, boolean randomInit, int maxExecutions) {
		this.delay = delay;
		if (randomInit) {
			lastTrigger = (float) (Math.random() * delay);
		} else {
			lastTrigger = 0;
		}
		this.maxExecutions = maxExecutions;
	}

	public void update(float timeDelta) {
		lastTrigger += timeDelta;

		int newTriggers = (int) (lastTrigger / delay);
		lastTrigger -= newTriggers * delay;

		if (maxExecutions >= 0) {
			newTriggers = Math.min(maxExecutions, newTriggers);
			maxExecutions -= newTriggers;
		}

		triggerAmount += newTriggers;
	}

	public boolean trigger() {
		boolean trigger = triggerAmount > 0;
		if (trigger) triggerAmount--;
		return trigger;
	}

	@Override
	public String toString() {
		return "EventTimer{" +
				"delay=" + delay +
				", lastTrigger=" + lastTrigger +
				", maxExecutions=" + maxExecutions +
				", triggerAmount=" + triggerAmount +
				'}';
	}

}
