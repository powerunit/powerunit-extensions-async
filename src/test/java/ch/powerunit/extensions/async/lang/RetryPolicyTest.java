package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class RetryPolicyTest implements TestSuite {
	@Test
	public void testRetryPolicyOperation() {
		RetryPolicy c1 = RetryPolicy.of(0, 1000);
		assertThat(c1).isNotNull();
		assertThat(c1.getCount()).is(0);
		c1.sleepBetweenRetry(1);
	}

	@Test
	public void tetRetryPolicyInterrupt() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		Thread subThread = new Thread(() -> {
			RetryPolicy.of(1, 10000).sleepBetweenRetry(1);
		});
		subThread.start();
		subThread.interrupt();
		subThread.join();
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(lessThan(5000L));
	}

	@Test
	public void tetRetryPolicySleepFromMs() {
		LocalDateTime start = LocalDateTime.now();
		RetryPolicy.of(1, 5000).sleepBetweenRetry(1);
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(greaterThanOrEqualTo(5000L));
	}

}
