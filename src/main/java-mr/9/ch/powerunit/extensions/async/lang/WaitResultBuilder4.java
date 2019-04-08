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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Fourth Step of the builder of {@link CompletableFuture} to specify the amount
 * of time between the retry.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder4<T> {
	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param value
	 *            the ms delay
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @since 1.0.0
	 */
	WaitResultBuilder5<T> everyMs(long value);

	/**
	 * Specify to retry every minute.
	 * 
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder5<T> everyMinute() {
		return every(Duration.ofMinutes(1));
	}

	/**
	 * Specify to retry every second.
	 * 
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder5<T> everySecond() {
		return every(Duration.ofSeconds(1));
	}

	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param value
	 *            the amount
	 * @param unit
	 *            the time unit
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @see TimeUnit
	 */
	default WaitResultBuilder5<T> every(int value, TimeUnit unit) {
		return everyMs(requireNonNull(unit, "unit can't be null").toMillis(value));
	}

	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param delay
	 *            the duration to be used
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @since 1.0.0
	 * @see Duration
	 */
	default WaitResultBuilder5<T> every(Duration delay) {
		return everyMs(requireNonNull(delay, "delay can't be null").toMillis());
	}

	/**
	 * Repeat as fast as possible.
	 * 
	 * @return {@link WaitResultBuilder5 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder5<T> asFastAsPossible() {
		return everyMs(1);
	}

}
