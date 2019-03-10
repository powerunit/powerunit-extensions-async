/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.Arrays;
import java.util.Objects;
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

	/**
	 * Specify the condition that doesn't accept the result.
	 * 
	 * @param notAcceptingClause
	 *            the {@link Predicate} to validate the result
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingNot(Predicate<T> notAcceptingClause) {
		return expecting(Objects.requireNonNull(notAcceptingClause, "notAcceptingClause can't be null").negate());
	}

	/**
	 * Specify that at least one condition must accept the result.
	 * 
	 * @param acceptingClause1
	 *            {@link Predicate first condition} to accept the result.
	 * @param next
	 *            all the following condition to accept the result.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingAnyOf(Predicate<T> acceptingClause1,
			@SuppressWarnings("unchecked") Predicate<T>... next) {
		Predicate<T> base = Objects.requireNonNull(acceptingClause1, "acceptingClause1 can't be null");
		return expecting(Arrays.stream(next).reduce(base, Predicate::or));
	}

	/**
	 * Specify that at alls conditions must accept the result.
	 * 
	 * @param acceptingClause1
	 *            {@link Predicate first condition} to accept the result.
	 * @param next
	 *            all the following condition to accept the result.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	default WaitResultBuilder3<T> expectingAllOf(Predicate<T> acceptingClause1,
			@SuppressWarnings("unchecked") Predicate<T>... next) {
		Predicate<T> base = Objects.requireNonNull(acceptingClause1, "acceptingClause1 can't be null");
		return expecting(Arrays.stream(next).reduce(base, Predicate::and));

	}
}
