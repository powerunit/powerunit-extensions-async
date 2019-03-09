/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;

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
}