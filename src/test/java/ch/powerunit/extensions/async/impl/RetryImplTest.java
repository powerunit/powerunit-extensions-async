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
package ch.powerunit.extensions.async.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.RetryPolicies;

public class RetryImplTest implements TestSuite {

	private static class MyCallable implements Callable<String> {

		public String result;

		@Override
		public String call() throws Exception {
			return result;
		}

	}

	// One1

	@Test
	public void testOneRetryOK() {
		RetryImpl<String> retry = new RetryImpl<String>(
				new WaitResultImpl<>(() -> Optional.of("X"), RetryPolicies.of(1, 10000)));
		LocalDateTime start = LocalDateTime.now();
		// First
		assertThat(retry.next()).is(true);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(true);
		assertThat(retry.getPreviousException()).isNull();

		// Second
		assertThat(retry.next()).is(false);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(true);
		assertThat(retry.getPreviousException()).isNull();

		// Time
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(lessThan(5000L));
	}

	@Test
	public void testOneRetryKO() {
		RetryImpl<String> retry = new RetryImpl<String>(
				new WaitResultImpl<>(Optional::empty, RetryPolicies.of(1, 10000)));
		LocalDateTime start = LocalDateTime.now();
		// First
		assertThat(retry.next()).is(true);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(false);
		assertThat(retry.getPreviousException()).isNull();

		// Second
		assertThat(retry.next()).is(false);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(false);
		assertThat(retry.getPreviousException()).isNull();

		// Time
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(lessThan(5000L));
	}

	@Test
	public void testOneRetryException() {

		RetryImpl<String> retry = new RetryImpl<String>(new WaitResultImpl<>(() -> {
			throw new IllegalArgumentException("test");
		}, RetryPolicies.of(1, Duration.ofSeconds(10))));
		LocalDateTime start = LocalDateTime.now();
		// First
		assertThat(retry.next()).is(true);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(false);
		assertThat(retry.getPreviousException()).isNotNull();

		// Second
		assertThat(retry.next()).is(false);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(false);
		assertThat(retry.getPreviousException()).isNotNull();

		// Time
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(lessThan(5000L));
	}

	// Two
	@Test
	public void testTwoRetryOK() {
		MyCallable test1 = new MyCallable();
		RetryImpl<String> retry = new RetryImpl<>(new WaitResultImpl<>(
				() -> Optional.ofNullable(test1.call()).filter(Objects::nonNull), RetryPolicies.of(2, 2000)));
		LocalDateTime start = LocalDateTime.now();
		// First
		assertThat(retry.next()).is(true);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(false);
		assertThat(retry.getPreviousException()).isNull();

		// Second
		test1.result = "X";
		assertThat(retry.next()).is(true);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(true);
		assertThat(retry.getPreviousException()).isNull();

		// Third
		assertThat(retry.next()).is(false);
		assertThat(retry.getResult()).isNotNull();
		assertThat(retry.getResult().isPresent()).is(true);
		assertThat(retry.getPreviousException()).isNull();

		// Time
		LocalDateTime end = LocalDateTime.now();
		long duration = Duration.between(start, end).toMillis();
		assertThat(duration).is(lessThan(4000L));
		assertThat(duration).is(greaterThanOrEqualTo(2000L));
	}

}
