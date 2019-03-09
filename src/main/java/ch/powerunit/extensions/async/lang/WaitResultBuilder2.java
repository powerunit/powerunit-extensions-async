/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * Second Step of the builder of {@link CompletableFuture} to specify the
 * condition to accept a result.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder2<T> {
	/**
	 * Specify the condition that accept the result.
	 * 
	 * @param acceptingClause
	 *            the {@link Predicate} to validate the result
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 */
	WaitResultBuilder3<T> expecting(Predicate<T> acceptingClause);
}
