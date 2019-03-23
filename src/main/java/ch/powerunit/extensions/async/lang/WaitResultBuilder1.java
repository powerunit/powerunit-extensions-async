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
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;

/**
 * First Step of the builder of {@link CompletableFuture} to skip, if necessary,
 * the error.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder1<T> extends WaitResultBuilder2<T> {

	/**
	 * Ignore any error during execution of the callable and define if there are not
	 * result and an exception at last operation if this exception must be thrown.
	 * 
	 * @param alsoDontFailWhenNoResultAndException
	 *            true if the last exception must also be ignored
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 */
	WaitResultBuilder2<T> ignoreException(boolean alsoDontFailWhenNoResultAndException);

	/**
	 * Ignore any error during execution of the callable.
	 * 
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 */
	default WaitResultBuilder2<T> ignoreException() {
		return ignoreException(true);
	}

	/**
	 * Explicitly indicate that no exception must be ignored.
	 * <p>
	 * This this the normal behaviour. This method may be used to make it explicit
	 * in the code.
	 * 
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder2<T> dontIgnoreException() {
		return this;
	}
}
