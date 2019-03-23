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

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Last Step of the builder of {@link CompletableFuture} to create the
 * completable operation it self.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 * @since 1.0.0
 */
public interface WaitResultBuilder6<T> {

	/**
	 * Create and start the async execution of the {@link CompletableFuture} the
	 * executor that was defined before.
	 * 
	 * @return the {@link CompletableFuture}
	 */
	CompletableFuture<Optional<T>> asyncExec();

	/**
	 * Directly wait for the result of this execution. In case of not ignored
	 * exception, an {@link AssertionError} is thrown.
	 * 
	 * @return the {@link Optional} with the result of the execution
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception.
	 */
	default Optional<T> finish() {
		try {
			return asyncExec().get();
		} catch (InterruptedException | ExecutionException e) {
			throw Optional.ofNullable(e.getCause()).filter(c -> c instanceof AssertionError)
					.map(AssertionError.class::cast)
					.orElseGet(() -> new AssertionError("Unexpected error " + e.getMessage(), e));
		}
	}

	/**
	 * Directly wait for a positive result of this execution. In case of not ignored
	 * exception, or when no result are available, an {@link AssertionError} is
	 * thrown.
	 * 
	 * @return the value if available
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception or missing result.
	 */
	default T finishWithAResult() {
		return finish().orElseThrow(() -> new AssertionError("No result is available"));
	}

	/**
	 * Shortcut method to the join of the {@link CompletableFuture}.
	 * <p>
	 * Only runtime exception in case of error.
	 * 
	 * @return the result of the wait
	 * @since 1.0.0
	 * @see CompletableFuture#join()
	 */
	default Optional<T> join() {
		return asyncExec().join();
	}

	/**
	 * Shortcut method to the join of the {@link CompletableFuture}, that expect a
	 * positive result.
	 * <p>
	 * Only runtime exception in case of error.
	 * 
	 * @return the result of the wait
	 * @since 1.0.0
	 * @see CompletableFuture#join()
	 */
	default T joinWithAResult() {
		return join().orElseThrow(() -> new AssertionError("No result is available when one is expected"));
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} and
	 * directly register a Consumer on the result.
	 * 
	 * @param action
	 *            the action to be done on the result
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenAccept(Consumer)
	 */
	default CompletableFuture<Void> thenAccept(Consumer<? super Optional<T>> action) {
		return asyncExec().thenAccept(action);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} and
	 * directly register a Function on the result.
	 * 
	 * @param fn
	 *            then function to be applied
	 * 
	 * @param <U>
	 *            The new return type
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenApply(Function)
	 */
	default <U> CompletableFuture<U> thenApply(Function<? super Optional<T>, ? extends U> fn) {
		return asyncExec().thenApply(fn);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} and
	 * exceptionally completes this CompletableFuture with a TimeoutException if not
	 * otherwise completed before the given timeout.
	 * 
	 * @param timeout
	 *            how long to wait before completing exceptionally with a
	 *            TimeoutException, in units of unit
	 * @param unit
	 *            a TimeUnit determining how to interpret the timeout parameter
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#orTimeout(long, TimeUnit)
	 */
	default CompletableFuture<Optional<T>> orTimeout(long timeout, TimeUnit unit) {
		return asyncExec().orTimeout(timeout, unit);
	}

	/**
	 * Returns a new CompletionStage that is completed normally based on this
	 * {@link CompletableFuture}.
	 *
	 * @return the new CompletionStage
	 * @since 1.0.0
	 * @see CompletableFuture#minimalCompletionStage()
	 */
	default CompletionStage<Optional<T>> minimalCompletionStage​() {
		return asyncExec().minimalCompletionStage();
	}

	/**
	 * Waits if necessary for the produced future to complete, and then returns its
	 * result.
	 *
	 * @return the result value
	 * @throws CancellationException
	 *             if the produced future was cancelled
	 * @throws ExecutionException
	 *             if the produced future completed exceptionally
	 * @throws InterruptedException
	 *             if the current thread was interrupted while waiting
	 * @see CompletableFuture#get()
	 * @since 1.0.0
	 */
	default Optional<T> get() throws InterruptedException, ExecutionException {
		return asyncExec().get();
	}
}
