/**
 * 
 */
package ch.powerunit.extensions.async.lang;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;

import ch.powerunit.extensions.async.impl.WaitResultImpl;
import ch.powerunit.extensions.async.lang.WaitResultBuilder1;
import ch.powerunit.extensions.async.lang.WaitResultBuilder2;
import ch.powerunit.extensions.async.lang.WaitResultBuilder3;

/**
 * This class is the entry point for this library to support async operation
 * inside test.
 * <p>
 * Use one of the provided methods to repeatedly get some data, until some
 * condition.
 * 
 * @since 1.0.0 - Before, this class was under
 *        {@link ch.powerunit.extensions.async}. This change is linked with java
 *        9 module, as it will not be possible to have the implementation in a
 *        sub package of the exported one.
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
	 * CompletableFuture&lt;Optional&lt;MyObject&gt;&gt; exec = WaitResult.of(MyObject::myCallable).expecting(MyObject::myControl)
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
		return new WaitResultBuilder1<T>() {

			@Override
			public WaitResultBuilder3<T> expecting(Predicate<T> acceptingClause) {
				return retry -> WaitResultBuilder5.of(new WaitResultImpl<>(action, acceptingClause, retry)::get);
			}

			@Override
			public WaitResultBuilder2<T> ignoreException(boolean alsoDontFailWhenNoResultAndException) {
				return predicate -> retry -> WaitResultBuilder5
						.of(new WaitResultImpl<>(action, alsoDontFailWhenNoResultAndException, predicate, retry)::get);
			}
		};
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * execution of the received action, with repetition until some condition.
	 * <p>
	 * In this case, it is assumed that the received action throws unchecked
	 * exception when the condition is not yet OK. The returned Optional will be
	 * present in case of success.
	 * 
	 * @param action
	 *            the {@link Runnable} to be executed.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 */
	public static WaitResultBuilder3<Boolean> ofRunnable(Runnable action) {
		return of(Executors.callable(action, true)).ignoreException(true).expecting(b -> b);
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

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control on a method returning a boolean.
	 * 
	 * @param conditionSupplier
	 *            the boolean supplier
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 */
	public static WaitResultBuilder3<Boolean> onCondition(Supplier<Boolean> conditionSupplier) {
		return of(conditionSupplier::get).expecting(b -> b);
	}
	
}
