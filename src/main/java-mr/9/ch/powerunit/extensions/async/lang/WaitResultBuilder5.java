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

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Almost final Step of the builder of {@link CompletableFuture} to define the
 * executor it self.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder5<T> extends Supplier<Optional<T>> {

	/**
	 * Directly wait for the result of this execution (the execution is run in this
	 * thread). In case of not ignored exception, an {@link AssertionError} is
	 * thrown.
	 * <p>
	 * 
	 * For example :
	 * 
	 * <pre>
	 * WaitResult.of(myCallable).dontIgnoreException().expecting(myPredicate).repeat(2)
	 * 		.every(1000, TimeUnit.MILLISECONDS).get()
	 * </pre>
	 * <ul>
	 * <li>If an exception occurs in {@code myCallable}, an {@link AssertionError}
	 * is thrown</li>
	 * <li>If no result is available, after the 2 try, an empty {@link Optional} is
	 * returned.</li>
	 * <li>Or the result is available in the returned {@link Optional}.</li>
	 * </ul>
	 * 
	 * @return the {@link Optional} with the result of the execution
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception.
	 */
	@Override
	Optional<T> get();

	/**
	 * Register an action to be done when the retrieval is finish (in success or
	 * not).
	 * <p>
	 * This may be used, for example, to release resources. This method may be used
	 * several times. In this case, all the registered action will be executed on
	 * Finish, starting by the first one.
	 * 
	 * @param action
	 *            the action to be done. May be null (ignored).
	 * @return a new instance of {@link WaitResultBuilder5} with this new action to
	 *         be done at the end.
	 * @since 1.1.0
	 */
	default WaitResultBuilder5<T> onFinish(Runnable action) {
		if (action == null) {
			return this;
		}
		return () -> {
			try {
				return get();
			} finally {
				action.run();
			}
		};
	}

	/**
	 * Define the executor to be used for the async part.
	 * <p>
	 * Both the action to retry and the control on the result will be executed on
	 * the thread provided by the executor.
	 * 
	 * @param executor
	 *            the executor to be used. This can't be null.
	 * @return {@link WaitResultBuilder6 the final step}
	 */
	default WaitResultBuilder6<T> using(Executor executor) {
		return () -> supplyAsync(this, executor);
	}

	/**
	 * Define the executor to be used for the async part as using
	 * {@link ForkJoinPool#commonPool()}.
	 * <p>
	 * Both the action to retry and the control on the result will be executed on
	 * the thread provided by the executor.
	 * 
	 * @return {@link WaitResultBuilder6 the final step}
	 * @see ForkJoinPool#commonPool()
	 */
	default WaitResultBuilder6<T> usingDefaultExecutor() {
		return using(commonPool());
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture}.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return the {@link CompletableFuture}
	 * @deprecated Replaced by {@code using(executor).asyncExec()}
	 */
	@Deprecated
	default CompletableFuture<Optional<T>> asyncExec(Executor executor) {
		return using(executor).asyncExec();
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()}.
	 * 
	 * @return the {@link CompletableFuture}
	 * @see #usingDefaultExecutor()
	 * @see WaitResultBuilder6#asyncExec()
	 */
	default CompletableFuture<Optional<T>> asyncExec() {
		return usingDefaultExecutor().asyncExec();
	}

	/**
	 * Directly wait for the result of this execution. In case of not ignored
	 * exception, an {@link AssertionError} is thrown.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * 
	 * @return the {@link Optional} with the result of the execution
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception.
	 * @deprecated Replaced by {@code using(executor).finish()}
	 */
	@Deprecated
	default Optional<T> finish(Executor executor) {
		return using(executor).finish();
	}

	/**
	 * Directly wait for the result of this execution using
	 * {@link ForkJoinPool#commonPool()}. In case of not ignored exception, an
	 * {@link AssertionError} is thrown.
	 * 
	 * @return the {@link Optional} with the result of the execution
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception.
	 * @see #usingDefaultExecutor()
	 * @see WaitResultBuilder6#finish()
	 */
	default Optional<T> finish() {
		return usingDefaultExecutor().finish();
	}

	/**
	 * Directly wait for a positive result of this execution. In case of not ignored
	 * exception, or when no result are available, an {@link AssertionError} is
	 * thrown.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * 
	 * @return the value if available
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception or missing result.
	 * @deprecated Replaced by {@code using(executor).finishWithAResult()}
	 */
	@Deprecated
	default T finishWithAResult(Executor executor) {
		return using(executor).finishWithAResult();
	}

	/**
	 * Directly wait for a positive result of this execution using
	 * {@link ForkJoinPool#commonPool()}. In case of not ignored exception, or when
	 * no result are available, an {@link AssertionError} is thrown.
	 * 
	 * @return the value if available
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception or missing result.
	 * @see #usingDefaultExecutor()
	 * @see WaitResultBuilder6#finishWithAResult()
	 */
	default T finishWithAResult() {
		return usingDefaultExecutor().finishWithAResult();
	}

	/**
	 * Add a mapper fonction, on the result, if applicable. This mapper is executed
	 * in the target thread.
	 * 
	 * @param mapper
	 *            the function to convert the result.
	 * @param <U>
	 *            the target of the mapper.
	 * @return the {@link WaitResultBuilder5} continuation of the builder
	 * @see Optional#map(Function)
	 * @since 1.0.0
	 */
	default <U> WaitResultBuilder5<U> map(Function<T, U> mapper) {
		return () -> get().map(mapper);
	}

	/**
	 * Add a flat mapper fonction, on the result, if applicable. This mapper is
	 * executed in the target thread.
	 * 
	 * @param mapper
	 *            the function to convert the result.
	 * @param <U>
	 *            the target of the mapper.
	 * @return the {@link WaitResultBuilder5} continuation of the builder
	 * @see Optional#flatMap(Function)
	 * @since 1.1.0
	 */
	default <U> WaitResultBuilder5<U> flatMap(Function<T, Optional<U>> mapper) {
		return () -> get().flatMap(mapper);
	}

	/**
	 * Add a or operation, on the result, if applicable. This or is executed in the
	 * target thread.
	 * <p>
	 * <b>Only available on java 9</b>
	 * 
	 * @param supplier
	 *            the function to convert the result
	 * @return the {@link WaitResultBuilder5} continuation of the builder
	 * @see Optional#or(Supplier)
	 * @since 1.1.0
	 */
	default WaitResultBuilder5<T> or(Supplier<? extends Optional<? extends T>> supplier) {
		return () -> get().or(supplier);
	}

	/**
	 * Add a filter predicate, on the result, if applicable. This filter is executed
	 * in the target thread.
	 * 
	 * @param filter
	 *            the filter
	 * @return the {@link WaitResultBuilder5} continuation of the builder
	 * @see Optional#filter(Predicate)
	 * @since 1.0.0
	 */
	default WaitResultBuilder5<T> filter(Predicate<T> filter) {
		return () -> get().filter(filter);
	}
}
