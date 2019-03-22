package ch.powerunit.extensions.async.lang;

/**
 * This interface can be used to specify retry configuration.
 * 
 * @author borettim
 * @since 1.0.0
 * @see RetryPolicies
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
}
