package ch.powerunit.extensions.async.impl;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
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
import java.util.function.Function;
import java.util.function.Predicate;

public final class FilePool implements Callable<Collection<WatchEvent<Path>>> {

	private static final Predicate<WatchEvent<?>> IGNORE_OVERFLOW = e -> !Objects.equals(e.kind(), OVERFLOW);

	@SuppressWarnings("unchecked")
	private static final Function<WatchEvent<?>, WatchEvent<Path>> COERCE_TO_PATH = e -> (WatchEvent<Path>) e;

	private final Path directory;
	private final WatchEvent.Kind<?> events[];

	private WatchService watcher; // Late init
	private WatchKey key; // Late init

	public FilePool(Path directory, WatchEvent.Kind<?>... events) {
		this.directory = directory;
		this.events = events;
	}

	@Override
	public Collection<WatchEvent<Path>> call() throws Exception {
		initIfNeeded();
		try {
			return key.pollEvents().stream().filter(IGNORE_OVERFLOW).map(COERCE_TO_PATH)
					.collect(collectingAndThen(toList(), Collections::unmodifiableList));
		} finally {
			key.reset();
		}
	}

	public void close() {
		Optional.ofNullable(watcher).ifPresent(FilePool::safeCloseWatchService);
	}

	private void initIfNeeded() throws IOException {
		if (watcher == null) {
			watcher = directory.getFileSystem().newWatchService();
			key = directory.register(watcher, events);
		}
	}

	private static void safeCloseWatchService(WatchService watcher) {
		try {
			watcher.close();
		} catch (IOException e) {
			// ignore
		}
	}

}