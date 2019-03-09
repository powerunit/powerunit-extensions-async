/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.CompletableFuture;

/**
 * First Step of the builder of {@link CompletableFuture} to skip, if necessary, the
 * error.
 * 
 * @param <T> The type of result of the {@link CompletableFuture}
 *
 */
public interface WaitResultBuilder1<T> extends WaitResultBuilder2<T> {

	/**
	 * Ignore any error during execution of the callable and define if there are not
	 * result and an exception at last operation if this exception must be thrown.
	 * 
	 * @param alsoDontFailWhenNoResultAndException
	 *            true if the last exception must also be ignored
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 */
	WaitResultBuilder2<T> ignoreException(boolean alsoDontFailWhenNoResultAndException);

	/**
	 * Ignore any error during execution of the callable.
	 * 
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 */
	default WaitResultBuilder2<T> ignoreException() {
		return ignoreException(true);
	}
}
