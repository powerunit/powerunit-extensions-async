/**
 * 
 */
package ch.powerunit.extensions.async.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.powerunit.extensions.async.lang.WaitResultBuilder1;
import ch.powerunit.extensions.async.lang.WaitResultBuilder2;
import ch.powerunit.extensions.async.lang.WaitResultBuilder3;
import ch.powerunit.extensions.async.lang.WaitResultBuilder4;
import ch.powerunit.extensions.async.lang.WaitResultBuilder5;

/**
 * @author borettim
 *
 */
public final class WaitResultImpl<T> implements WaitResultBuilder1<T>, WaitResultBuilder2<T>, WaitResultBuilder3<T>,
		WaitResultBuilder4<T>, WaitResultBuilder5<T>, Supplier<Optional<T>> {

	private final Callable<T> action;

	private final boolean ignoreException;

	private final boolean alsoDontThrowLastExceptionWhenNoResult;

	private final Predicate<T> acceptingClause;

	private final int count;

	private final long waitInMs;

	public WaitResultImpl(Callable<T> action) {
		this.action = Objects.requireNonNull(action, "action can't be null");
		this.ignoreException = false;
		this.alsoDontThrowLastExceptionWhenNoResult = false;
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
		this.ignoreException = ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = alsoDontFailWhenNoResultAndException;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, Predicate<T> acceptingClause) {
		this.action = prev.action;
		this.ignoreException = prev.ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = prev.alsoDontThrowLastExceptionWhenNoResult;
		this.count = prev.count;
		this.waitInMs = prev.waitInMs;
		this.acceptingClause = acceptingClause;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, int count) {
		this.action = prev.action;
		this.ignoreException = prev.ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = prev.alsoDontThrowLastExceptionWhenNoResult;
		this.acceptingClause = prev.acceptingClause;
		this.waitInMs = prev.waitInMs;
		this.count = count;
	}

	private WaitResultImpl(WaitResultImpl<T> prev, long millis) {
		this.action = prev.action;
		this.ignoreException = prev.ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = prev.alsoDontThrowLastExceptionWhenNoResult;
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
	public WaitResultBuilder2<T> ignoreException(boolean alsoDontFailWhenNoResultAndException) {
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
	public WaitResultBuilder3<T> expecting(Predicate<T> acceptingClause) {
		return new WaitResultImpl<T>(this, acceptingClause);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder3#repeat(int)
	 */
	@Override
	public WaitResultBuilder4<T> repeat(int count) {
		return new WaitResultImpl<T>(this, count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.powerunit.extensions.async.lang.WaitResultBuilder4#every(int,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public WaitResultBuilder5<T> every(int value, TimeUnit unit) {
		return new WaitResultImpl<T>(this, Objects.requireNonNull(unit, "unit can't be null").toMillis(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.powerunit.extensions.async.lang.WaitResultBuilder5#asyncExec(java.util.
	 * concurrent.Executor)
	 */
	@Override
	public CompletableFuture<Optional<T>> asyncExec(Executor executor) {
		return CompletableFuture.supplyAsync(this);
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
				T result = action.call();
				if (acceptingClause.test(result)) {
					return Optional.ofNullable(result);
				}
			} catch (Exception e) {
				prev = e;
				if (ignoreException) {
					continue;
				}
			}
			sleepBetweenRetry();
		}
		if (prev != null && !alsoDontThrowLastExceptionWhenNoResult) {
			throw new AssertionError("Unable to obtains the result, because of " + prev.getMessage()
					+ " ; Original error class is " + prev.getClass(), prev);
		}
		return Optional.empty();
	}

}
