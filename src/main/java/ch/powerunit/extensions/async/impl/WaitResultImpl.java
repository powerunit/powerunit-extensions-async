/**
 * 
 */
package ch.powerunit.extensions.async.impl;

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
