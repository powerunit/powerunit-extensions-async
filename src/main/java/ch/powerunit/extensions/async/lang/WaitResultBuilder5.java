/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
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
	WaitResultBuilder6<T> using(Executor executor);

	/**
	 * Define the executor to be used for the async part as using
	 * {@link ForkJoinPool#commonPool()}.
	 * 
	 * @return {@link WaitResultBuilder6 the final step}
	 */
	default WaitResultBuilder6<T> usingDefaultExecutor() {
		return using(ForkJoinPool.commonPool());
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
	 * @deprecated Replaced by {@code usingDefaultExecutor().asyncExec()}
	 */
	@Deprecated
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
	 * @deprecated Replaced by {@code usingDefaultExecutor().finish()}
	 */
	@Deprecated
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
	 * @deprecated Replaced by {@code usingDefaultExecutor().finishWithAResult()}
	 */
	@Deprecated
	default T finishWithAResult() {
		return usingDefaultExecutor().finishWithAResult();
	}

	/**
	 * Used internally to create the builder
	 * 
	 * @param supplier
	 *            the supplier
	 * @param <T>
	 *            The type of the target optional
	 * @return the instance
	 */
	static <T> WaitResultBuilder5<T> of(Supplier<Optional<T>> supplier) {
		return new WaitResultBuilder5<T>() {

			@Override
			public Optional<T> get() {
				return supplier.get();
			}

			@Override
			public WaitResultBuilder6<T> using(Executor executor) {
				return () -> CompletableFuture.supplyAsync(this, executor);
			}
		};
	}
}
