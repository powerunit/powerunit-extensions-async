/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Last Step of the builder of {@link CompletableFuture} to create the
 * completable operation it self.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder5<T> {

	/**
	 * Create and start the async execution of the {@link CompletableFuture}.
	 * 
	 * @param executor
	 *            the executor to be used.
	 * @return the {@link CompletableFuture}
	 */
	CompletableFuture<Optional<T>> asyncExec(Executor executor);

	/**
	 * Create ahd start the async execution of the {@link CompletableFuture} using
	 * {@link ForkJoinPool#commonPool()}.
	 * 
	 * @return the {@link CompletableFuture}
	 */
	default CompletableFuture<Optional<T>> asyncExec() {
		return asyncExec(ForkJoinPool.commonPool());
	}

}
