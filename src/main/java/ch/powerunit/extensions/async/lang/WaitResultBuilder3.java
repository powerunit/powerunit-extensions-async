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
	 * Specify a retry clause.
	 * <p>
	 * The goal is here to define somewhere in the test a constant with this clause
	 * and reuse it in the test.
	 * 
	 * @param retry
	 *            the retry clause.
	 * @return {@link WaitResultBuilder5 the final step of the builder}
	 * @since 1.0.0
	 */
	WaitResultBuilder5<T> repeat(RetryPolicy retry);

	/**
	 * Specify the maximal number of retry.
	 * 
	 * @param count
	 *            the number of retry
	 * @return {@link WaitResultBuilder4 the next step of the builder}
	 */
	default WaitResultBuilder4<T> repeat(int count) {
		return value -> repeat(RetryPolicies.of(count, value));
	}

	/**
	 * Specify that only one retry will be done (so only one execution and one
	 * validation).
	 * 
	 * @return {@link WaitResultBuilder5 the final step of the builder}
	 */
	default WaitResultBuilder5<T> repeatOnlyOnce() {
		return repeat(RetryPolicies.RETRY_ONLY_ONCE);
	}

	/**
	 * Specify that only two retry will be done.
	 * 
	 * @return {@link WaitResultBuilder4 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder4<T> repeatTwice() {
		return repeat(2);
	}
}