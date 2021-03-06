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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import ch.powerunit.Rule;
import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.rules.TemporaryFolder;

public class WaitFileTest implements TestSuite {

	@Rule
	public final TemporaryFolder folder = temporaryFolder();

	@Test
	public void testFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Collection<Path>>> wait = WaitFile.newFileIn(test).expecting(l -> !l.isEmpty())
				.repeat(5).everySecond().usingDefaultExecutor().asyncExec();
		Thread.sleep(1010);
		new File(test.toFile(), "test").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}

	@Test
	public void testFoundNamed() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Path>> wait = WaitFile.newFileNamedIn(test, "test2").expecting(l -> true).repeat(5)
				.everySecond().usingDefaultExecutor().asyncExec();
		Thread.sleep(500);
		new File(test.toFile(), "test").mkdir();
		Thread.sleep(700);
		new File(test.toFile(), "test2").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}

	@Test
	public void testNeverFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Collection<Path>>> wait = WaitFile.newFileIn(test).expecting(l -> !l.isEmpty())
				.repeat(2).everySecond().usingDefaultExecutor().asyncExec();
		assertThat(wait.join()).is(optionalIsNotPresent());
	}

	@Test
	public void testNeverFoundButCreateBeforeStartOfExecution() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		WaitResultBuilder6<Collection<Path>> wait = WaitFile.newFileIn(test).expecting(l -> !l.isEmpty()).repeat(2)
				.everySecond().usingDefaultExecutor();
		new File(test.toFile(), "test").mkdir();
		assertThat(wait.join()).is(optionalIsNotPresent());
	}

	@Test
	public void testRemoved() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		new File(test.toFile(), "test").mkdir();
		CompletableFuture<Optional<Collection<Path>>> wait = WaitFile.removeFileFrom(test).expecting(l -> !l.isEmpty())
				.repeat(3).everySecond().usingDefaultExecutor().asyncExec();
		Thread.sleep(1010);
		new File(test.toFile(), "test").delete();
		assertThat(wait.join()).is(optionalIsPresent());
	}

	@Test
	public void testNeverRemoved() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Collection<Path>>> wait = WaitFile.removeFileFrom(test).expecting(l -> !l.isEmpty())
				.repeat(2).everySecond().usingDefaultExecutor().asyncExec();
		assertThat(wait.join()).is(optionalIsNotPresent());
	}

	@Test
	public void testEventFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Collection<WatchEvent<Path>>>> wait = WaitFile
				.eventIn(test, StandardWatchEventKinds.ENTRY_CREATE).expecting(l -> !l.isEmpty()).repeat(3)
				.everySecond().usingDefaultExecutor().asyncExec();
		Thread.sleep(1010);
		new File(test.toFile(), "test").mkdir();
		assertThat(wait.join()).is(optionalIsPresent());
	}

	@Test
	public void testEventNeverFound() throws IOException, InterruptedException {
		Path test = folder.newFolder();
		CompletableFuture<Optional<Collection<WatchEvent<Path>>>> wait = WaitFile
				.eventIn(test, StandardWatchEventKinds.ENTRY_CREATE).expecting(l -> !l.isEmpty()).repeat(2)
				.everySecond().usingDefaultExecutor().asyncExec();
		assertThat(wait.join()).is(optionalIsNotPresent());
	}

}
