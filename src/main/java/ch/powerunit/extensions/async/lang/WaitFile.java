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
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Callable;

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
	 * Wait for a folder to have some event.
	 * <p>
	 * The wait starts at the first try to get the result.
	 * 
	 * @param directory
	 *            the directory to be verified.
	 * @param events
	 *            the events to wait for.
	 * @return {@link WaitResultBuilder1} the next step of the builder.
	 */
	@SafeVarargs
	public static WaitResultBuilder1<Collection<WatchEvent<Path>>> eventIn(Path directory, Kind<Path>... events) {
		requireNonNull(directory, "directory can't be null");
		FilePool filePool = new FilePool(directory, events);
		return WaitResult.of(filePool, filePool::close);
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
		return WaitResult.of(toPathCollection(filePool), filePool::close);
	}

	/**
	 * Wait for a folder to contains new entry based on his name.
	 * <p>
	 * The wait starts at the first try to get the result.
	 * 
	 * @param directory
	 *            the directory to be verified.
	 * @param name
	 *            the expected name
	 * @return {@link WaitResultBuilder1} the next step of the builder.
	 */
	public static WaitResultBuilder1<Path> newFileNamedIn(Path directory, String name) {
		requireNonNull(directory, "directory can't be null");
		requireNonNull(name, "name can't be null");
		FilePool filePool = new FilePool(directory, ENTRY_CREATE);
		return WaitResult.of(toPathByName(toPathCollection(filePool), name), filePool::close);
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
		return WaitResult.of(toPathCollection(filePool), filePool::close);
	}

	private static Callable<Collection<Path>> toPathCollection(Callable<Collection<WatchEvent<Path>>> callable) {
		return () -> callable.call().stream().map(WatchEvent::context)
				.collect(collectingAndThen(toList(), Collections::unmodifiableList));
	}

	private static Callable<Path> toPathByName(Callable<Collection<Path>> callable, String name) {
		return () -> callable.call().stream()
				.filter(p -> Objects.equals(p.getName(p.getNameCount() - 1).toString(), name)).findFirst().orElse(null);
	}

}
