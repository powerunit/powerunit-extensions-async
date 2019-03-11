/**
 * 
 */
package ch.powerunit.extensions.async.impl;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import ch.powerunit.extensions.async.lang.RetryClause;
import ch.powerunit.extensions.async.lang.WaitResultBuilder;
import ch.powerunit.extensions.async.lang.WaitResultBuilder5;

/**
 * @author borettim
 *
 */
public final class WaitResultImpl<T> implements WaitResultBuilder<T>, Callable<Optional<T>> {

	private final Callable<T> action;

	private final ExceptionHandler exceptionHandler;

	private final Predicate<T> acceptingClause;

	private final RetryClause retryClause;

	public WaitResultImpl(Callable<T> action) {
		this.action = requireNonNull(action, "action can't be null");
		this.exceptionHandler = new ExceptionHandler(false, false);
		this.acceptingClause = null;
		this.retryClause = null;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, ExceptionHandler exceptionHandler) {
		this.action = prev.action;
		this.acceptingClause = prev.acceptingClause;
		this.retryClause = prev.retryClause;
		this.exceptionHandler = exceptionHandler;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, Predicate<T> acceptingClause) {
		this.action = prev.action;
		this.exceptionHandler = prev.exceptionHandler;
		this.retryClause = prev.retryClause;
		this.acceptingClause = acceptingClause;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, RetryClause retryClause) {
		this.action = prev.action;
		this.exceptionHandler = prev.exceptionHandler;
		this.acceptingClause = prev.acceptingClause;
		this.retryClause = retryClause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder1#ignoreException(
	 * boolean)
	 */
	@Override
	public WaitResultImpl<T> ignoreException(boolean alsoDontFailWhenNoResultAndException) {
		return new WaitResultImpl<T>(this, new ExceptionHandler(true, alsoDontFailWhenNoResultAndException));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.powerunit.extensions.async.lang.WaitResultBuilder2#expecting(java.util.
	 * function.Predicate)
	 */
	@Override
	public WaitResultImpl<T> expecting(Predicate<T> acceptingClause) {
		return new WaitResultImpl<T>(this, acceptingClause);
	}

	@Override
	public WaitResultBuilder5<T> repeat(RetryClause retry) {
		return new WaitResultImpl<T>(this, Objects.requireNonNull(retry, "retry can't be null"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder3#repeat(int)
	 */
	@Override
	public WaitResultImpl<T> repeat(int count) {
		return new WaitResultImpl<T>(this, RetryClause.of(count, 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder4#everyMs(long)
	 */
	@Override
	public WaitResultImpl<T> everyMs(long value) {
		return new WaitResultImpl<T>(this, retryClause.withMs(value));
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
