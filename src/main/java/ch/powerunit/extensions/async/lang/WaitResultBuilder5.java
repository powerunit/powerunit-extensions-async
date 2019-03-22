/**
 * 
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
	 * 
	 * @return the {@link Optional} with the result of the execution
	 * 
	 * @throws AssertionError
	 *             In case of not ignored exception.
	 */
	@Override
	Optional<T> get();

	/**
	 * Define the executor to be used for the async part.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return {@link WaitResultBuilder6 the final step}
	 */
	default WaitResultBuilder6<T> using(Executor executor) {
		return () -> supplyAsync(this, executor);
	}

	/**
	 * Define the executor to be used for the async part as using
	 * {@link ForkJoinPool#commonPool()}.
	 * 
	 * @return {@link WaitResultBuilder6 the final step}
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
