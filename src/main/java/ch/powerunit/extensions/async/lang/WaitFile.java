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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Collection;

import ch.powerunit.extensions.async.impl.FilePool;

/**
 * This class provides methods to wait for fileSystem events.
 * 
 * @since 1.1.0
 *
 */
public final class WaitFile {
	private WaitFile() {
	}

	/**
	 * Wait for a folder to contains new entry.
	 * <p>
	 * The wait starts at the first try to get the result.
	 * 
	 * @param directory
	 *            the directory to be verified.
	 * @return {@link WaitResultBuilder1} the next step of the builder.
	 */
	public static WaitResultBuilder1<Collection<Path>> newFileIn(Path directory) {
		requireNonNull(directory, "directory can't be null");
		FilePool filePool = new FilePool(directory, ENTRY_CREATE);
		return WaitResult.of(filePool, filePool::close);
	}

	/**
	 * Wait for a folder to have entry removed.
	 * <p>
	 * The wait starts at the first try to get the result.
	 * 
	 * @param directory
	 *            the directory to be verified.
	 * @return {@link WaitResultBuilder1} the next step of the builder.
	 */
	public static WaitResultBuilder1<Collection<Path>> removeFileFrom(Path directory) {
		requireNonNull(directory, "directory can't be null");
		FilePool filePool = new FilePool(directory, ENTRY_DELETE);
		return WaitResult.of(filePool, filePool::close);
	}

}
