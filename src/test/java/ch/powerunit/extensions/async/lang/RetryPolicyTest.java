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

import java.time.Duration;
import java.time.LocalDateTime;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class RetryPolicyTest implements TestSuite {
	@Test
	public void testRetryPolicyOperation() {
		RetryPolicy c1 = RetryPolicies.of(0, 1000);
		assertThat(c1).isNotNull();
		assertThat(c1.getCount()).is(0);
		c1.sleepBetweenRetry(1);
	}

	@Test
	public void testRetryPolicyInterrupt() throws InterruptedException {
		LocalDateTime start = LocalDateTime.now();
		Thread subThread = new Thread(() -> {
			RetryPolicies.of(1, 10000).sleepBetweenRetry(1);
		});
		subThread.start();
		subThread.interrupt();
		subThread.join();
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(lessThan(5000L));
	}

	@Test
	public void testRetryPolicySleepFromMs() {
		LocalDateTime start = LocalDateTime.now();
		RetryPolicies.of(1, 2000).sleepBetweenRetry(1);
		LocalDateTime end = LocalDateTime.now();
		assertThat(Duration.between(start, end).toMillis()).is(greaterThanOrEqualTo(2000L));
	}

}
