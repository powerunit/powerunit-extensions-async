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
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * This class provides methods to wait for fileSystem events.
 * 
 * @since 1.1.0
 *
 */
public final class WaitFile {
	private WaitFile() {
	}

	private static final class FilePool implements Callable<Collection<Path>> {

		private final Path directory;
		private final WatchEvent.Kind<?> events[];

		private WatchService watcher; // Late init
		private WatchKey key; // Late init

		public FilePool(Path directory, WatchEvent.Kind<?>... events) {
			this.directory = directory;
			this.events = events;
		}

		@Override
		public Collection<Path> call() throws Exception {
			if (watcher == null) {
				watcher = directory.getFileSystem().newWatchService();
				key = directory.register(watcher, events);
			}
			try {
				return key.pollEvents().stream().filter(e -> !Objects.equals(e.kind(), OVERFLOW))
						.map(WatchEvent::context).map(Path.class::cast)
						.collect(collectingAndThen(toList(), Collections::unmodifiableList));
			} finally {
				key.reset();
			}
		}

		public void close() {
			Optional.ofNullable(watcher).ifPresent(WaitFile::safeCloseWatchService);
		}

	}

	/**
	 * Wait for a folder to contains new entry.
	 * <p>
	 * The wait start at the first try to get the result.
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

	private static void safeCloseWatchService(WatchService watcher) {
		try {
			watcher.close();
		} catch (IOException e) {
			// ignore
		}
	}
}
