package ch.powerunit.extensions.async.lang;

import java.time.Duration;

/**
 * This interface can be used to specify retry configuration.
 * 
 * @author borettim
 * @since 1.0.0
 */
public interface RetryPolicy {
	/**
	 * This is the number of retry to be done.
	 * 
	 * @return the number of retry.
	 */
	int getCount();

	/**
	 * This is the method called to wait between a retry.
	 * 
	 * @param retry
	 *            the current retry (start at 1).
	 */
	void sleepBetweenRetry(int retry);

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
		return RetryPolicies.of(count, ms);
	}

	/**
	 * Create a new RetryPolicy.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param waitDuration
	 *            the duration to wait.
	 * @return the RetryPolicy.
	 */
	static RetryPolicy of(int count, Duration waitDuration) {
		return RetryPolicies.of(count, waitDuration);
	}
}
