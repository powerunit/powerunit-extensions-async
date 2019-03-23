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

/**
 * This interface can be used to specify retry configuration.
 * 
 * @author borettim
 * @since 1.0.0
 * @see RetryPolicies
 */
public interface RetryPolicy {
	/**
	 * This is the number of retry to be done.
	 * 
	 * @return the number of retry.
	 */
	int getCount();

	/**
	 * This is the method called to wait between a retry.
	 * 
	 * @param retry
	 *            the current retry (start at 1).
	 */
	void sleepBetweenRetry(int retry);
}
