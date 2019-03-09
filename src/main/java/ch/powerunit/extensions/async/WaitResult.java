/**
 * 
 */
package ch.powerunit.extensions.async;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import ch.powerunit.extensions.async.impl.WaitResultImpl;
import ch.powerunit.extensions.async.lang.WaitResultBuilder1;
import ch.powerunit.extensions.async.lang.WaitResultBuilder2;

/**
 * This class is the entry point for this library to support async operation
 * inside test.
 * <p>
 * Use one of the provided methods to repeatedly get some data, until some
 * condition.
 *
 */
public final class WaitResult {
	private WaitResult() {
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * the result of the received action, with repetition until some condition.
	 * <p>
	 * <h1>Simple sample</h1>
	 * 
	 * <pre>
	 * CompletableFuture&lt;Optional&lt;MyObject&gt;&gt; exec = WaitResult.of(myObject::myCallable).expecting(myObject::myControl)
	 * 		.repeat(10).every(10, TimeUnit.MILLISECONDS).asyncExec();
	 * </pre>
	 * 
	 * @param action
	 *            the {@link Callable} providing the result.
	 * @param <T>
	 *            The type of the result.
	 * @return {@link WaitResultBuilder1 the next step of the builder}
	 */
	public static <T> WaitResultBuilder1<T> of(Callable<T> action) {
		return new WaitResultImpl<T>(action);
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control on the mutable object.
	 * <p>
	 * <h1>Simple sample</h1>
	 * 
	 * <pre>
	 * CompletableFuture&lt;Optional&lt;MyObject&gt;&gt; exec = WaitResult.on(myObject).expecting(MyObject::myControl).repeat(100)
	 * 		.every(10, TimeUnit.MILLISECONDS).asyncExec();
	 * </pre>
	 * 
	 * @param mutableObject
	 *            the mutable object to be checked.
	 * @param <T>
	 *            The type of the result.
	 * @return {@link WaitResultBuilder2 the next step of the builder}
	 */
	public static <T> WaitResultBuilder2<T> on(T mutableObject) {
		return of(() -> mutableObject);
	}
}
