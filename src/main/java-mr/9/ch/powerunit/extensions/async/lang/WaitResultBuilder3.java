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

import java.util.concurrent.CompletableFuture;

/**
 * Third Step of the builder of {@link CompletableFuture} to specify the maximal
 * number of retry.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder3<T> {

	/**
	 * Specify a retry clause.
	 * <p>
	 * The goal is here to define somewhere in the test a constant with this clause
	 * and reuse it in the test.
	 * 
	 * @param retry
	 *            the retry clause.
	 * @return {@link WaitResultBuilder5 the final step of the builder}
	 * @since 1.0.0
	 * @see RetryPolicies
	 */
	WaitResultBuilder5<T> repeat(RetryPolicy retry);

	/**
	 * Specify the maximal number of retry.
	 * 
	 * @param count
	 *            the number of retry
	 * @return {@link WaitResultBuilder4 the next step of the builder}
	 */
	default WaitResultBuilder4<T> repeat(int count) {
		return value -> repeat(RetryPolicies.of(count, value));
	}

	/**
	 * Specify that only one retry will be done (so only one execution and one
	 * validation).
	 * 
	 * @return {@link WaitResultBuilder5 the final step of the builder}
	 */
	default WaitResultBuilder5<T> repeatOnlyOnce() {
		return repeat(RetryPolicies.RETRY_ONLY_ONCE);
	}

	/**
	 * Specify that only two retry will be done.
	 * 
	 * @return {@link WaitResultBuilder4 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder4<T> repeatTwice() {
		return repeat(2);
	}
}