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

/**
 * @author borettim
 *
 */
class ExceptionHandler {// package protected
	private final boolean ignoreException;

	private final boolean alsoDontThrowLastExceptionWhenNoResult;

	public ExceptionHandler(boolean ignoreException, boolean alsoDontThrowLastExceptionWhenNoResult) {
		this.ignoreException = ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = alsoDontThrowLastExceptionWhenNoResult;
	}

	public void handleException(Exception e) {
		if (e != null && !ignoreException) {
			throw new AssertionError("Unable to obtains the result during one try, because of " + e.getMessage()
					+ " ; Original error class is " + e.getClass(), e);
		}
	}

	public void handleFinalException(Exception e) {
		if (e != null && !alsoDontThrowLastExceptionWhenNoResult) {
			throw new AssertionError("Unable to obtains the result and finish in error, because of " + e.getMessage()
					+ " ; Original error class is " + e.getClass(), e);
		}
	}

	@Override
	public String toString() {
		return "ExceptionHandler [ignoreException=" + ignoreException + ", alsoDontThrowLastExceptionWhenNoResult="
				+ alsoDontThrowLastExceptionWhenNoResult + "]";
	}
}
