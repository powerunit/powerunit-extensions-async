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
