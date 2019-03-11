package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.util.Objects;

/**
 * This interface can be used to specify retry configuration.
 * 
 * @author borettim
 * @since 1.0.0
 */
public interface RetryClause {
	/**
	 * This is the number of retry to be done.
	 * 
	 * @return the number of retry.
	 */
	int getCount();

	/**
	 * This is the wait (in ms) between retry.
	 * 
	 * @return the wait time.
	 */
	long getWaitInMs();

	/**
	 * Create a new RetryClause, with this retry count.
	 * 
	 * @param count
	 *            the number of retry
	 * @return the RetryClause
	 */
	default RetryClause withCount(int count) {
		return of(count, getWaitInMs());
	}

	/**
	 * Create a new RetryClause, with this wait time in ms.
	 * 
	 * @param ms
	 *            the wait time
	 * @return the RetryClause
	 */
	default RetryClause withMs(long ms) {
		return of(getCount(), ms);
	}

	/**
	 * Create a new RetryClause.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param ms
	 *            the wait time in ms.
	 * @return the RetryClause
	 */
	static RetryClause of(int count, long ms) {
		return new RetryClause() {

			@Override
			public long getWaitInMs() {
				return ms;
			}

			@Override
			public int getCount() {
				return count;
			}
		};
	}

	/**
	 * Create a new RetryClause.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param waitDuration
	 *            the duration to wait.
	 * @return the RetryClause.
	 */
	static RetryClause of(int count, Duration waitDuration) {
		return of(count, Objects.requireNonNull(waitDuration, "waitDuration can't be null").toMillis());
	}
}
