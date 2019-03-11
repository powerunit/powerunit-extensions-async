/**
 * 
 */
package ch.powerunit.extensions.async.impl;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import ch.powerunit.extensions.async.lang.RetryClause;
import ch.powerunit.extensions.async.lang.WaitResultBuilder5;

/**
 * @author borettim
 *
 */
public final class WaitResultImpl<T> implements WaitResultBuilder5<T>, Callable<Optional<T>> {

	private final Callable<T> action;

	private final ExceptionHandler exceptionHandler;

	private final Predicate<T> acceptingClause;

	private final RetryClause retryClause;

	public WaitResultImpl(Callable<T> action, boolean ignoreException, boolean alsoDontFailWhenNoResultAndException,
			Predicate<T> acceptingClause, RetryClause retryClause) {
		this.action = requireNonNull(action, "action can't be null");
		this.exceptionHandler = new ExceptionHandler(ignoreException, alsoDontFailWhenNoResultAndException);
		this.acceptingClause = requireNonNull(acceptingClause, "acceptingClause can't be null");
		this.retryClause = requireNonNull(retryClause, "retryClause can't be null");
	}

	@Override
	public Optional<T> get() {
		RetryImpl<T> retry = new RetryImpl<>(retryClause, this);
		while (retry.next()) {
			exceptionHandler.handleException(retry.getPreviousException());
			Optional<T> result = retry.getResult();
			if (result.isPresent()) {
				return result;
			}
		}
		exceptionHandler.handleFinalException(retry.getPreviousException());
		return Optional.empty();
	}

	@Override
	public Optional<T> call() throws Exception {
		return Optional.ofNullable(action.call()).filter(acceptingClause);
	}

}
