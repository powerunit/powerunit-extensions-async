package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.util.Objects;
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
	 *            the ms delay
	 * @return {@link WaitResultBuilder5 the last step of the builder}
	 * @since 1.0.0
	 */
	WaitResultBuilder5<T> everyMs(long value);

	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param value
	 *            the amount
	 * @param unit
	 *            the time unit
	 * @return {@link WaitResultBuilder5 the last step of the builder}
	 */
	default WaitResultBuilder5<T> every(int value, TimeUnit unit) {
		return everyMs(Objects.requireNonNull(unit, "unit can't be null").toMillis(value));
	}

	/**
	 * Specify the amount of time between retry.
	 * 
	 * @param delay
	 *            the duration to be used
	 * @return {@link WaitResultBuilder5 the last step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder5<T> every(Duration delay) {
		return everyMs(Objects.requireNonNull(delay, "delay can't be null").toMillis());
	}
}
