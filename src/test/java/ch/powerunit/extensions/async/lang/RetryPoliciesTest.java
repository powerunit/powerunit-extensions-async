package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class RetryPoliciesTest implements TestSuite {

	@Test
	public void testRetryPolicySleepFromTimeUnit() {
		LocalDateTime start = LocalDateTime.now();
		RetryPolicies.of(1, 2, TimeUnit.SECONDS).sleepBetweenRetry(1);
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(greaterThanOrEqualTo(2000L));
	}

	@Test
	public void testRetryPolicySleepFromDuration() {
		LocalDateTime start = LocalDateTime.now();
		RetryPolicies.of(1, Duration.ofSeconds(2)).sleepBetweenRetry(1);
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(greaterThanOrEqualTo(2000L));
	}

	@Test
	public void testRetryPolicySleepIncremental() {
		LocalDateTime start = LocalDateTime.now();
		RetryPolicy rp = RetryPolicies.ofIncremental(3, Duration.ofSeconds(1));
		assertThat(rp.getCount()).is(3);
		rp.sleepBetweenRetry(1);
		rp.sleepBetweenRetry(2);
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(greaterThanOrEqualTo(3000L));
	}
}
