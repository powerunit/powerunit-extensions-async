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
 */package ch.powerunit.extensions.async.impl;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.powerunit.extensions.async.lang.RetryPolicy;

/**
 * @author borettim
 *
 */
public final class WaitResultImpl<T> implements Supplier<Optional<T>>, Callable<Optional<T>> {

	private final Callable<Optional<T>> action;

	private final ExceptionHandler exceptionHandler;

	private final RetryPolicy retryClause;

	private static <T> Callable<Optional<T>> asCallable(Callable<T> action, Predicate<T> acceptingClause) {
		requireNonNull(action, "action can't be null");
		requireNonNull(acceptingClause, "acceptingClause can't be null");
		return () -> ofNullable(action.call()).filter(acceptingClause);
	}

	public WaitResultImpl(Callable<T> action, boolean alsoDontFailWhenNoResultAndException,
			Predicate<T> acceptingClause, RetryPolicy retryClause) {
		this.action = asCallable(action, acceptingClause);
		this.exceptionHandler = new ExceptionHandler(true, alsoDontFailWhenNoResultAndException);
		this.retryClause = requireNonNull(retryClause, "retryClause can't be null");
	}

	public WaitResultImpl(Callable<T> action, Predicate<T> acceptingClause, RetryPolicy retryClause) {
		this.action = asCallable(action, acceptingClause);
		this.exceptionHandler = new ExceptionHandler(false, false);
		this.retryClause = requireNonNull(retryClause, "retryClause can't be null");
	}

	@Override
	public Optional<T> get() {
		RetryImpl<T> retry = new RetryImpl<>(this);
		while (retry.next()) {
			exceptionHandler.handleException(retry.getPreviousException());
			Optional<T> result = retry.getResult();
			if (result.isPresent()) {
				return result;
			}
		}
		exceptionHandler.handleFinalException(retry.getPreviousException());
		return empty();
	}

	public RetryPolicy getRetryClause() {
		return retryClause;
	}

	@Override
	public Optional<T> call() throws Exception {
		return action.call();
	}

}
