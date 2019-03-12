package ch.powerunit.extensions.async.impl;

import java.util.Optional;
import java.util.concurrent.Callable;

import ch.powerunit.extensions.async.lang.RetryPolicy;

class RetryImpl<T> { // package protected
	private final RetryPolicy retryClause;

	private final Callable<Optional<T>> callable;

	private int retryCount;

	private Optional<T> result;

	private Exception previousException;

	public RetryImpl(RetryPolicy retryClause, Callable<Optional<T>> callable) {
		this.retryClause = retryClause;
		this.callable = callable;
		this.retryCount = 0;
	}

	public boolean next() {
		if (retryCount >= retryClause.getCount()) {
			return false;
		}
		if (retryCount > 0) {
			retryClause.sleepBetweenRetry(retryCount);
		}
		retryCount++;
		previousException = null;
		try {
			result = callable.call();
		} catch (Exception e) {
			result = Optional.empty();
			previousException = e;
		}
		return true;
	}

	public Optional<T> getResult() {
		return result;
	}

	public Exception getPreviousException() {
		return previousException;
	}

}
