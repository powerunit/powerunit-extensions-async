package ch.powerunit.extensions.async.lang;

/**
 * Helpers method to build {@link RetryPolicy}.
 * 
 * @since 1.0.0
 *
 */
public final class RetryPolicies {
	private RetryPolicies() {
	}

	/**
	 * Create a new RetryPolicy.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param ms
	 *            the wait time in ms.
	 * @return the RetryPolicy
	 */
	static RetryPolicy of(int count, long ms) {
		return new RetryPolicy() {

			@Override
			public void sleepBetweenRetry(int retry) {
				RetryPolicies.sleepBetweenRetry(ms);
			}

			@Override
			public int getCount() {
				return count;
			}
		};
	}

	private static void sleepBetweenRetry(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
