package ch.powerunit.extensions.async.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.RetryPolicy;

public class RetryImplTest implements TestSuite {

	Predicate<String> TRUE = x -> true;

	Predicate<String> FALSE = x -> false;

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
				new WaitResultImpl<>(() -> "X", TRUE, RetryPolicy.of(1, 10000)));
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
				new WaitResultImpl<>(() -> "X", FALSE, RetryPolicy.of(1, 10000)));
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
		}, TRUE, RetryPolicy.of(1, Duration.ofSeconds(10))));
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
		RetryImpl<String> retry = new RetryImpl<>(new WaitResultImpl<>(test1, s -> s != null, RetryPolicy.of(2, 2000)));
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
