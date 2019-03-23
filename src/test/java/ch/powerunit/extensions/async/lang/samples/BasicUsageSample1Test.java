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
package ch.powerunit.extensions.async.lang.samples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestRule;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.WaitResult;
import ch.powerunit.extensions.async.lang.WaitResultBuilder5;

public class BasicUsageSample1Test implements TestSuite {

	private PrintStream output;

	@Rule
	public final TestRule rules = testListenerRuleOnStart((c) -> {
		try {
			output = new PrintStream(
					new FileOutputStream("target/" + BasicUsageSample1Test.class.getName() + ".log", true));
			output.println(c.getFullTestName() + ":");
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("" + e.getMessage(), e);
		}
	}).around(after(() -> Optional.ofNullable(output).ifPresent(PrintStream::close)));

	private Callable<String> myCallable = () -> "x";

	private Callable<String> myCallableWithException = () -> {
		throw new Exception("Sample");
	};

	private <T> Callable<T> displayThread(Callable<T> callable) {
		return () -> {
			output.println("callable:" + Thread.currentThread().getName());
			return callable.call();
		};
	}

	private <T> Predicate<T> displayThread(Predicate<T> predicate) {
		return t -> {
			output.println("predicate:" + Thread.currentThread().getName());
			return predicate.test(t);
		};
	}

	@Test
	public void testWaitResultOfBasicSample() {
		output.println(Thread.currentThread().getName());
		//@formatter:off
		Optional<String> result = WaitResult.
			of(displayThread(myCallable)).
			dontIgnoreException().
			expecting(displayThread(s->"x".equals(s))).
			repeat(2).
			every(1000, TimeUnit.MILLISECONDS).
			get();
		//@formatter:on
		assertThat(result).is(optionalIs("x"));
	}

	@Test
	public void testWaitResultOfBasicSampleAsync() {
		output.println(Thread.currentThread().getName());
		//@formatter:off
		Optional<String> result = WaitResult.
			of(displayThread(myCallable)).
			dontIgnoreException().
			expecting(displayThread(s->"x".equals(s))).
			repeat(2).
			every(1000, TimeUnit.MILLISECONDS).
			asyncExec().join();
		//@formatter:on
		assertThat(result).is(optionalIs("x"));
	}

	@Test
	public void testWaitResultOfBasicSampleWithException() {
		output.println(Thread.currentThread().getName());
		//@formatter:off
		WaitResultBuilder5<String> executor = WaitResult.
			of(displayThread(myCallableWithException)).
			dontIgnoreException().
			expecting(displayThread(s->"x".equals(s))).
			repeat(2).
			every(1000, TimeUnit.MILLISECONDS);
		//@formatter:on
		assertWhen(executor::get).throwException(instanceOf(AssertionError.class));
	}
}
