/**
 * 
 */
package ch.powerunit.extensions.async.lang.samples;

import java.time.Duration;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.RetryPolicies;
import ch.powerunit.extensions.async.lang.RetryPolicy;

public class RetryPoliciesSample1Test implements TestSuite {
	@Test
	public void testOfIncremental1() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.ofIncremental(
				3, 
				Duration.ofMillis(50)
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}

	@Test
	public void testOfIncremental2() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.ofIncremental(
				3, 
				20
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}
}
