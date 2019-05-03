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
import java.util.function.Supplier;

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
	 * Create a new RetryPolicy with constant wait time.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.of(3, 12);
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait 12 ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait 12 ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
	 * 
	 * @param count
	 *            the number of retry.
	 * @param ms
	 *            the constant wait time in ms.
	 * @return the RetryPolicy
	 */
	public static RetryPolicy of(int count, long ms) {
		return of(count, addToString(l -> ms, () -> String.format("Constant wait time of %s ms", ms)));
	}

	/**
	 * Create a new RetryPolicy with constant wait time.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.of(3, 15, TimeUnit.MILLISECONDS);
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait 15 ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait 15 ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
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
	 * Create a new RetryPolicy with constant wait time.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.of(3, Duration.ofMillis(10));
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait 10 ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait 10 ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
	 * 
	 * @param count
	 *            the number of retry.
	 * @param duration
	 *            the constant duration to wait.
	 * @return the RetryPolicy
	 */
	public static RetryPolicy of(int count, Duration duration) {
		return of(count, requireNonNull(duration, "duration can't be null").toMillis());
	}

	/**
	 * Create a new RetryPolicy, by using a generic function to compute the retry
	 * duration.
	 * <p>
	 * For example, using this definition :
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.of(3, c -&gt; (long) (Math.random() * 100));
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait a random time between 0 and 100 ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait a random time between 0 and 100 ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
	 * 
	 * @param count
	 *            the number of retry. A value of 1 is to do only one try, without
	 *            sleep.
	 * @param retryToWaitTime
	 *            the function to compute the wait time based on the retry. The
	 *            received value by this function starts with 1, and this will be
	 *            the first retry (meaning a the initial try has been done before).
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

			@Override
			public String toString() {
				return String.format("total count = %s, with sleep method = %s", count, retryToWaitTime);
			}
		};
	}

	/**
	 * Create a new RetryPolicy, that wait each time more time : first time the
	 * received duration, second time twice, etc.
	 * <p>
	 * For example, using this definition :
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.ofIncremental(3, 20);
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait 20ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait 40ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
	 * 
	 * @param count
	 *            the number of retry.
	 * @param ms
	 *            the time in ms that will be combined with the retry number
	 * @return the RetryPolicy
	 */
	public static RetryPolicy ofIncremental(int count, long ms) {
		return of(count, addToString(retry -> retry * ms, () -> String.format("Incremental retry based on %s ms", ms)));
	}

	/**
	 * Create a new RetryPolicy, that wait each time more time : first time the
	 * received duration, second time twice, etc.
	 * <p>
	 * For example, using this definition :
	 * 
	 * <pre>
	 * RetryPolicy incremental = RetryPolicies.ofIncremental(3, Duration.ofMillis(50));
	 * </pre>
	 * 
	 * Then following of (re-)tries will be (maximum retries):
	 * <ol>
	 * <li>Do a first try</li>
	 * <li>Wait 50ms</li>
	 * <li>Do a second retry</li>
	 * <li>Wait 100ms</li>
	 * <li>Do a third retry</li>
	 * </ol>
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

	private static IntToLongFunction addToString(IntToLongFunction target, Supplier<String> toString) {
		return new IntToLongFunction() {

			@Override
			public long applyAsLong(int value) {
				return target.applyAsLong(value);
			}

			@Override
			public String toString() {
				return toString.get();
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
