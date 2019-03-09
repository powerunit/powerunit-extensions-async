/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Third Step of the builder of {@link CompletableFuture} to specify the maximal
 * number of retry.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder3<T> {
	/**
	 * Specify the maximal number of retry.
	 * 
	 * @param count
	 *            the number of retry
	 * @return {@link WaitResultBuilder4 the next step of the builder}
	 */
	WaitResultBuilder4<T> repeat(int count);

	/**
	 * Specify that only one retry will be done (so only one execution and one
	 * validation).
	 * 
	 * @return {@link WaitResultBuilder5 the final step of the builder}
	 */
	default WaitResultBuilder5<T> repeatOnlyOnce() {
		return repeat(1).every(1, TimeUnit.MILLISECONDS);
	}
}