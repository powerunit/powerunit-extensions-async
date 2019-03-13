/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
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
	 * Create and start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()}.
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
	 * Shortcut method to the join of the {@link CompletableFuture}.
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
}
