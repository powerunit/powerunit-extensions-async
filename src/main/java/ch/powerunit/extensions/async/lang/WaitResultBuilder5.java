/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Last Step of the builder of {@link CompletableFuture} to create the
 * completable operation it self.
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
	 * Create and start the async execution of the {@link CompletableFuture}.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return the {@link CompletableFuture}
	 */
	default CompletableFuture<Optional<T>> asyncExec(Executor executor) {
		return CompletableFuture.supplyAsync(this);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()}.
	 * 
	 * @return the {@link CompletableFuture}
	 */
	default CompletableFuture<Optional<T>> asyncExec() {
		return asyncExec(ForkJoinPool.commonPool());
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
	 */
	default Optional<T> finish(Executor executor) {
		try {
			return asyncExec(executor).get();
		} catch (InterruptedException e) {
			throw new AssertionError("Unable to get the result, because of " + e.getMessage(), e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof AssertionError) {
				throw (AssertionError) e.getCause();
			}
			throw new AssertionError("Unexpected error " + e.getMessage(), e);
		}
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
		return finish(ForkJoinPool.commonPool());
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
	 */
	default T finishWithAResult(Executor executor) {
		return finish(executor).orElseThrow(() -> new AssertionError("No result is available"));
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
		return finishWithAResult(ForkJoinPool.commonPool());
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
		return join(ForkJoinPool.commonPool());
	}

	/**
	 * Shortcut method to the join of the {@link CompletableFuture}.
	 * <p>
	 * Only runtime exception in case of error.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return the result of the wait
	 * @since 1.0.0
	 * @see CompletableFuture#join()
	 */
	default T joinWithAResult(Executor executor) {
		return join(executor).orElseThrow(() -> new AssertionError("No result is available when one is expected"));
	}

	/**
	 * Shortcut method to the join of the {@link CompletableFuture}, expecting a
	 * result.
	 * <p>
	 * Only runtime exception in case of error.
	 * 
	 * @return the result of the wait if available
	 * @since 1.0.0
	 * @see CompletableFuture#join()
	 */
	default T joinWithAResult() {
		return joinWithAResult(ForkJoinPool.commonPool());
	}

	/**
	 * Shortcut method to the join of the {@link CompletableFuture}.
	 * <p>
	 * Only runtime exception in case of error.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return the result of the wait
	 * @since 1.0.0
	 * @see CompletableFuture#join()
	 */
	default Optional<T> join(Executor executor) {
		return asyncExec(executor).join();
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} and
	 * directly register a Consumer on the result.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @param action
	 *            the action to be done on the result
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenAccept(Consumer)
	 */
	default CompletableFuture<Void> thenAccept(Executor executor, Consumer<? super Optional<T>> action) {
		return asyncExec(executor).thenAccept(action);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()} and directly register a Consumer on the
	 * result.
	 * 
	 * @param action
	 *            the action to be done on the result
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenAccept(Consumer)
	 */
	default CompletableFuture<Void> thenAccept(Consumer<? super Optional<T>> action) {
		return thenAccept(ForkJoinPool.commonPool(), action);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} and
	 * directly register a Function on the result.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @param fn
	 *            then function to be applied
	 * 
	 * @param <U>
	 *            The new return type
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenApply(Function)
	 */
	default <U> CompletableFuture<U> thenApply(Executor executor, Function<? super Optional<T>, ? extends U> fn) {
		return asyncExec(executor).thenApply(fn);
	}

	/**
	 * Create and start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()} and directly register a Function on the
	 * result.
	 * 
	 * @param fn
	 *            then function to be applied
	 * @param <U>
	 *            The new return type
	 * @return the {@link CompletableFuture}
	 * @since 1.0.0
	 * @see CompletableFuture#thenApply(Function)
	 */
	default <U> CompletableFuture<U> thenApply(Function<? super Optional<T>, ? extends U> fn) {
		return thenApply(ForkJoinPool.commonPool(), fn);
	}
}
