/**
 * 
 */
package ch.powerunit.extensions.async.lang.samples;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

	@Test
	public void testOfGeneric() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.of(
				3, 
				c->(long)(Math.random()*100)
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}

	@Test
	public void testOfConstant1() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.of(
				3, 
				Duration.ofMillis(10)
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}

	@Test
	public void testOfConstant2() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.of(
				3, 
				15,
				TimeUnit.MILLISECONDS
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}

	@Test
	public void testOfConstant3() {
		//@formatter:off
		RetryPolicy incremental = 
			RetryPolicies.of(
				3, 
				12
			);
		//@formatter:on
		incremental.sleepBetweenRetry(1);
		incremental.sleepBetweenRetry(2);
		System.out.println(incremental);
	}
}
