/**
 * Powerunit - A JDK1.8 test framework
 * Copyright (C) 2014 Mathieu Boretti.
 *
 * This file is part of Powerunit
 *
 * Powerunit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Powerunit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Powerunit. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.powerunit.extensions.async.lang;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.Executors.callable;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
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
 * @since 1.1.0 - Starting from version 1.1.0, the {@link System.Logger} feature
 *        is used to do some logging of the system. One goal is to provide a way
 *        to the user to see when the system is waiting for example. Also, some
 *        methods were added to decorate {@link Callable} and {@link Predicate}
 *        to add toString to describe them.
 */
public final class WaitResult {
	private WaitResult() {
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * the result of the received action, with repetition until some condition.
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
	 * @throws NullPointerException
	 *             if action is null
	 */
	public static <T> WaitResultBuilder1<T> of(Callable<T> action) {
		return of(action, null);
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * the result of the received action, with repetition until some condition.
	 * 
	 * @param action
	 *            the {@link Callable} providing the result.
	 * @param actionOnFinish
	 *            register an action after the result is retrieved (success or
	 *            failure). This may be used to register some resource cleanup.
	 * @param <T>
	 *            The type of the result.
	 * @return {@link WaitResultBuilder1 the next step of the builder}
	 * @throws NullPointerException
	 *             if action is null
	 * @since 1.1.0
	 */
	public static <T> WaitResultBuilder1<T> of(Callable<T> action, Runnable actionOnFinish) {
		requireNonNull(action, "action can't be null");
		return new WaitResultBuilder1<T>() {

			@Override
			public WaitResultBuilder3<T> expecting(Predicate<T> acceptingClause) {
				return retry -> ((WaitResultBuilder5<T>) new WaitResultImpl<>(
						asFilteredCallable(action, acceptingClause), retry)::get).onFinish(actionOnFinish);
			}

			@Override
			public WaitResultBuilder2<T> ignoreException(boolean alsoDontFailWhenNoResultAndException) {
				return predicate -> retry -> ((WaitResultBuilder5<T>) new WaitResultImpl<>(
						asFilteredCallable(action, predicate), alsoDontFailWhenNoResultAndException, retry)::get)
								.onFinish(actionOnFinish);
			}
		};
	}

	private static <T> Callable<Optional<T>> asFilteredCallable(Callable<T> action, Predicate<T> acceptingClause) {
		requireNonNull(action, "action can't be null");
		requireNonNull(acceptingClause, "acceptingClause can't be null");
		return callableWithToString(() -> ofNullable(action.call()).filter(acceptingClause),
				() -> String.format("Action = %s, AcceptingClause = %s", action, acceptingClause));
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * execution of the received action, with repetition until some condition.
	 * <p>
	 * 
	 * @param supplier
	 *            the {@link Supplier} to be executed.
	 * @param <T>
	 *            The type of the result.
	 * @return {@link WaitResultBuilder1 the next step of the builder}
	 * @since 1.1.0
	 * @throws NullPointerException
	 *             if supplier is null
	 */
	public static <T> WaitResultBuilder1<T> ofSupplier(Supplier<T> supplier) {
		requireNonNull(supplier, "supplier can't be null");
		return of(callableWithToString(supplier::get, () -> supplier.toString()));
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
	 * @throws NullPointerException
	 *             if action is null
	 */
	public static WaitResultBuilder3<Boolean> ofRunnable(Runnable action) {
		requireNonNull(action, "action can't be null");
		return of(callableWithToString(callable(action, true), () -> action.toString())).ignoreException(true)
				.expecting(predicateWithToString(b -> b, () -> "Expecting true"));
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control on the mutable object.
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
		return of(callableWithToString(() -> mutableObject, () -> String.format("on object %s", mutableObject)));
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control on a method returning a boolean.
	 * 
	 * @param conditionSupplier
	 *            the boolean supplier
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @throws NullPointerException
	 *             if conditionSupplier is null
	 */
	public static WaitResultBuilder3<Boolean> onCondition(Supplier<Boolean> conditionSupplier) {
		requireNonNull(conditionSupplier, "conditionSupplier can't be null");
		return of(callableWithToString(conditionSupplier::get, () -> conditionSupplier.toString()))
				.expecting(predicateWithToString(b -> b, () -> "Expecting true"));
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control of a call that is assumed as OK when an exception is thrown.
	 * 
	 * @param action
	 *            the action that is expected to thrown an exception.
	 * @return {@link WaitResultBuilder3 the next step of the builder}
	 * @since 1.0.0
	 * @throws NullPointerException
	 *             if action is null
	 */
	public static WaitResultBuilder3<Exception> forException(Callable<?> action) {
		requireNonNull(action, "action can't be null");
		return of(callableWithToString(() -> {
			try {
				action.call();
				return null;
			} catch (Exception e) {
				return e;
			}
		}, () -> action.toString())).dontIgnoreException().expectingNotNull();
	}

	/**
	 * Start the builder to create an instance of {@link CompletableFuture} based on
	 * repeated control of a call that is assumed as done when an exception a
	 * specific exception is throw.
	 * 
	 * @param action
	 *            the action that is expected to thrown an exception.
	 * @param targetException
	 *            the expected Exception class
	 * @param <T>
	 *            the expected exception type
	 * @return {@link WaitResultBuilder1 the next step of the builder}
	 * @since 1.1.0
	 * @throws NullPointerException
	 *             if action or targetException is null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Exception> WaitResultBuilder1<T> forException(Callable<?> action,
			Class<T> targetException) {
		requireNonNull(action, "action can't be null");
		requireNonNull(targetException, "targetException can't be null");
		return of(callableWithToString(() -> {
			try {
				action.call();
				return null;
			} catch (Exception e) {
				return (T) Optional.of(e).filter(c -> targetException.isAssignableFrom(c.getClass()))
						.orElseThrow(() -> e);
			}
		}, () -> action.toString()));
	}

	// Helper method for logging
	/**
	 * Modify a Callable to add a toString.
	 * 
	 * @param callable
	 *            the Callable to be decorated.
	 * @param toString
	 *            the Supplier to be used as toString method.
	 * @return the decorated Callable.
	 * @throws NullPointerException
	 *             if callable or toString is null.
	 * @since 1.1.0
	 */
	public static <T> Callable<T> callableWithToString(Callable<T> callable, Supplier<String> toString) {
		requireNonNull(callable, "callable can't be null");
		requireNonNull(toString, "toString can't be null");
		return new Callable<T>() {

			@Override
			public T call() throws Exception {
				return callable.call();
			}

			@Override
			public String toString() {
				return toString.get();
			}
		};
	}

	/**
	 * Modify a Predicate to add a toString.
	 * 
	 * @param predicate
	 *            the Predicate to be decorated.
	 * @param toString
	 *            the Supplier to be used as toString method.
	 * @return the decorated Predicate.
	 * @throws NullPointerException
	 *             if predicate or toString is null.
	 * @since 1.1.0
	 */
	public static <T> Predicate<T> predicateWithToString(Predicate<T> predicate, Supplier<String> toString) {
		requireNonNull(predicate, "predicate can't be null");
		requireNonNull(toString, "toString can't be null");
		return new Predicate<T>() {

			@Override
			public boolean test(T t) {
				return predicate.test(t);
			}

			@Override
			public String toString() {
				return toString.get();
			}
		};
	}

}
