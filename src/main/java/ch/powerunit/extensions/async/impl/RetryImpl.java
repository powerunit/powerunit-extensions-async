package ch.powerunit.extensions.async.impl;

import java.util.Optional;

import ch.powerunit.extensions.async.lang.RetryPolicy;

class RetryImpl<T> { // package protected
	private final WaitResultImpl<T> on;

	private int retryCount;

	private Optional<T> result;

	private Exception previousException;

	public RetryImpl(WaitResultImpl<T> on) {
		this.on = on;
		this.retryCount = 0;
	}

	public boolean next() {
		RetryPolicy retryClause = on.getRetryClause();
		if (retryCount >= retryClause.getCount()) {
			return false;
		}
		if (retryCount > 0) {
			retryClause.sleepBetweenRetry(retryCount);
		}
		retryCount++;
		previousException = null;
		try {
			result = on.call();
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
