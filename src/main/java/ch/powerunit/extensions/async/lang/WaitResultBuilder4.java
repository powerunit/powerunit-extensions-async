package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Fourth Step of the builder of {@link CompletableFuture} to specify the amount
 * of time between the retry.
 * 
 * @param <T>
 *            The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder4<T> {
	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param value
	 *            the amount
	 * @param unit
	 *            the time unit
	 * @return {@link WaitResultBuilder5 the last step of the builder}
	 */
	WaitResultBuilder5<T> every(int value, TimeUnit unit);
}
