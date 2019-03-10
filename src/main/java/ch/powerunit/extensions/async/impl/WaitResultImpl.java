/**
 * 
 */
package ch.powerunit.extensions.async.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import ch.powerunit.extensions.async.lang.WaitResultBuilder;

/**
 * @author borettim
 *
 */
public final class WaitResultImpl<T> implements WaitResultBuilder<T>, Callable<Optional<T>> {

	private final Callable<T> action;

	private final ExceptionHandler exceptionHandler;

	private final Predicate<T> acceptingClause;

	private final int count;

	private final long waitInMs;

	public WaitResultImpl(Callable<T> action) {
		this.action = Objects.requireNonNull(action, "action can't be null");
		this.exceptionHandler = new ExceptionHandler(false, false);
		this.acceptingClause = null;
		this.count = 0;
		this.waitInMs = 0;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, boolean ignoreException,
			boolean alsoDontFailWhenNoResultAndException) {
		this.action = prev.action;
		this.acceptingClause = prev.acceptingClause;
		this.count = prev.count;
		this.waitInMs = prev.waitInMs;
		this.exceptionHandler = new ExceptionHandler(ignoreException, alsoDontFailWhenNoResultAndException);
	}

	private WaitResultImpl(WaitResultImpl<T> prev, Predicate<T> acceptingClause) {
		this.action = prev.action;
		this.exceptionHandler = prev.exceptionHandler;
		this.count = prev.count;
		this.waitInMs = prev.waitInMs;
		this.acceptingClause = acceptingClause;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, int count) {
		this.action = prev.action;
		this.exceptionHandler = prev.exceptionHandler;
		this.acceptingClause = prev.acceptingClause;
		this.waitInMs = prev.waitInMs;
		this.count = count;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, long millis) {
		this.action = prev.action;
		this.exceptionHandler = prev.exceptionHandler;
		this.acceptingClause = prev.acceptingClause;
		this.count = prev.count;
		this.waitInMs = millis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder1#ignoreException(
	 * boolean)
	 */
	@Override
	public WaitResultImpl<T> ignoreException(boolean alsoDontFailWhenNoResultAndException) {
		return new WaitResultImpl<T>(this, true, alsoDontFailWhenNoResultAndException);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder3#repeat(int)
	 */
	@Override
	public WaitResultImpl<T> repeat(int count) {
		return new WaitResultImpl<T>(this, count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder4#everyMs(long)
	 */
	@Override
	public WaitResultImpl<T> everyMs(long value) {
		return new WaitResultImpl<T>(this, value);
	}

	private void sleepBetweenRetry() {
		try {
			Thread.sleep(waitInMs);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	@Override
	public Optional<T> get() {
		Exception prev = null;
		for (int i = 0; i < count; i++) {
			prev = null;
			try {
				Optional<T> result = call();
				if (result.isPresent()) {
					return result;
				}
			} catch (Exception e) {
				prev = e;
				exceptionHandler.handleException(e);
			}
			sleepBetweenRetry();
		}
		exceptionHandler.handleFinalException(prev);
		return Optional.empty();
	}

	@Override
	public Optional<T> call() throws Exception {
		return Optional.ofNullable(action.call()).filter(acceptingClause);
	}

}
