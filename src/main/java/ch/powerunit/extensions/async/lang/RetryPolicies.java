package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
	public static RetryPolicy of(int count, long ms) {
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

	/**
	 * Create a new RetryPolicy.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param value
	 *            the wait time
	 * @param unit
	 *            the unit of the wait time.
	 * @return the RetryPolicy
	 */
	public static RetryPolicy of(int count, long value, TimeUnit unit) {
		return of(count, Objects.requireNonNull(unit, "unit can't be null").toMillis(value));
	}

	/**
	 * Create a new RetryPolicy.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param duration
	 *            the duration to wait.
	 * @return the RetryPolicy
	 */
	public static RetryPolicy of(int count, Duration duration) {
		return of(count, Objects.requireNonNull(duration, "duration can't be null").toMillis());
	}

	/**
	 * Create a new RetryPolicy, that wait each time more time : first time the
	 * received duration, second time twice, etc.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param ms
	 *            the time in ms that will be combined with the retry number
	 * @return the RetryPolicy
	 */
	public static RetryPolicy ofIncremental(int count, long ms) {
		return new RetryPolicy() {

			@Override
			public void sleepBetweenRetry(int retry) {
				RetryPolicies.sleepBetweenRetry(ms * retry);
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
