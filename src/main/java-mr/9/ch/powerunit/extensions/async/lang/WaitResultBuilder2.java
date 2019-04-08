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

import static ch.powerunit.extensions.async.lang.WaitResult.predicateWithToString;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Second Step of the builder of {@link CompletableFuture} to specify the
 * condition to accept a result.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder2<T> {
	/**
	 * Specify the condition that accept the result.
	 * 
	 * @param acceptingClause
	 *            the {@link Predicate} to validate the result
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 */
	WaitResultBuilder3<T> expecting(Predicate<T> acceptingClause);

	/**
	 * Specify that the returned result must be equals to the received object.
	 * 
	 * @param other
	 *            the object to compare with. May be null.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @see Predicate#isEqual(Object)
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingEqualsTo(T other) {
		return expecting(
				predicateWithToString(Predicate.isEqual(other), () -> String.format("is equals to %s", other)));
	}

	/**
	 * Specify that the returned result must be not null.
	 * 
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @see Objects#nonNull(Object)
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingNotNull() {
		return expecting(predicateWithToString(Objects::nonNull, () -> "is not null"));
	}

	/**
	 * Specify the condition that doesn't accept the result.
	 * 
	 * @param notAcceptingClause
	 *            the {@link Predicate} to validate the result
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingNot(Predicate<T> notAcceptingClause) {
		requireNonNull(notAcceptingClause, "notAcceptingClause can't be null");
		return expecting(
				predicateWithToString(notAcceptingClause.negate(), () -> String.format("not %s", notAcceptingClause)));
	}

	/**
	 * Specify that at least one condition must accept the result.
	 * 
	 * @param acceptingClause1
	 *            {@link Predicate first condition} to accept the result.
	 * @param next
	 *            all the following condition to accept the result.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingAnyOf(Predicate<T> acceptingClause1,
			@SuppressWarnings("unchecked") Predicate<T>... next) {
		Predicate<T> base = requireNonNull(acceptingClause1, "acceptingClause1 can't be null");
		return expecting(stream(next).reduce(base, Predicate::or));
	}

	/**
	 * Specify that at all conditions must accept the result.
	 * 
	 * @param acceptingClause1
	 *            {@link Predicate first condition} to accept the result.
	 * @param next
	 *            all the following condition to accept the result.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingAllOf(Predicate<T> acceptingClause1,
			@SuppressWarnings("unchecked") Predicate<T>... next) {
		Predicate<T> base = requireNonNull(acceptingClause1, "acceptingClause1 can't be null");
		return expecting(stream(next).reduce(base, Predicate::and));

	}
}
