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
package ch.powerunit.extensions.async.lang.samples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.WaitFile;
import ch.powerunit.rules.TemporaryFolder;

public class WaitFileSample1Test implements TestSuite {

	@Rule
	public final TemporaryFolder folder = temporaryFolder();

	@Test
	public void testEventFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		//@formatter:off
		CompletableFuture<Optional<Collection<WatchEvent<Path>>>> wait = 
			WaitFile
				.eventIn(test, StandardWatchEventKinds.ENTRY_CREATE)
				.expecting(l -> l.stream()
						.map(WatchEvent::context)
						.map(Path::getFileName)
						.map(Path::toString)
						.anyMatch(n->n.equals("test"))
						)
				.repeat(3)
					.every(Duration.ofMillis(250))
				.usingDefaultExecutor()
					.asyncExec();
		//@formatter:off
		Thread.sleep(200);
		new File(test.toFile(), "test").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}
	
	@Test
	public void testNewFileFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		//@formatter:off
		CompletableFuture<Optional<Collection<Path>>> wait = 
			WaitFile
				.newFileIn(test)
				.expecting(l -> l.stream()
						.map(Path::getFileName)
						.map(Path::toString)
						.anyMatch(n->n.equals("test2"))
						)
				.repeat(4)
					.every(Duration.ofMillis(200))
				.usingDefaultExecutor()
					.asyncExec();
		//@formatter:off
		Thread.sleep(210);
		new File(test.toFile(), "test2").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}
	
	@Test
	public void testNewFileNamedFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		//@formatter:off
		CompletableFuture<Optional<Path>> wait = 
			WaitFile
				.newFileNamedIn(test,"test")
				.expectingNotNull()
				.repeat(3)
					.every(Duration.ofMillis(250))
				.usingDefaultExecutor()
					.asyncExec();
		//@formatter:off
		Thread.sleep(200);
		new File(test.toFile(), "test").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}

}
