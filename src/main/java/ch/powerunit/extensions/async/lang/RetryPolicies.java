/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.async.lang;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.IntToLongFunction;

/**
 * Helpers methods to build {@link RetryPolicy}.
 * 
 * @since 1.0.0
 *
 */
public final class RetryPolicies {

	/**
	 * Retry Policy to just do one try.
	 * 
	 */
	public static final RetryPolicy RETRY_ONLY_ONCE = of(1, 1);

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
		return of(count, l -> ms);
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
		return of(count, requireNonNull(unit, "unit can't be null").toMillis(value));
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
		return of(count, requireNonNull(duration, "duration can't be null").toMillis());
	}

	/**
	 * Create a new RetryPolicy.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param retryToWaitTime
	 *            the function to compute the wait time based on the retry.
	 * @return the RetryPolicy
	 */
	public static RetryPolicy of(int count, IntToLongFunction retryToWaitTime) {
		return new RetryPolicy() {

			@Override
			public void sleepBetweenRetry(int retry) {
				RetryPolicies.sleepBetweenRetry(retryToWaitTime.applyAsLong(retry));
			}

			@Override
			public int getCount() {
				return count;
			}
		};
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
		return of(count, retry -> retry * ms);
	}

	/**
	 * Create a new RetryPolicy, that wait each time more time : first time the
	 * received duration, second time twice, etc.
	 * 
	 * @param count
	 *            the number of retry.
	 * @param duration
	 *            the duration that will be combined with the retry number
	 * @return the RetryPolicy
	 */
	public static RetryPolicy ofIncremental(int count, Duration duration) {
		return ofIncremental(count, requireNonNull(duration, "duration can't be null").toMillis());
	}

	private static void sleepBetweenRetry(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
