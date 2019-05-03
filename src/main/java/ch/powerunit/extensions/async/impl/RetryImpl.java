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
package ch.powerunit.extensions.async.impl;

import static java.util.Optional.empty;

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
			result = empty();
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
