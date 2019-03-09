package ch.powerunit.extensions.async;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;

public class WaitResultTest implements TestSuite {

	private static class Handler {
		public boolean ok = false;
	}

	@Test
	public void testObjectMethodDirectlyOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expecting(o -> true).repeat(100)
				.every(10, TimeUnit.MILLISECONDS).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodNotDirectlyOK() throws InterruptedException, ExecutionException {
		Handler h = new Handler();
		CompletableFuture<Optional<Handler>> exec = WaitResult.on(h).expecting(o -> o.ok).repeat(10)
				.every(1, TimeUnit.SECONDS).asyncExec();
		Thread.sleep(2000);
		h.ok = true;
		Optional<Handler> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
		assertThat(result.get().ok).is(true);
	}

	@Test
	public void testNotIgnoreExceptionThenException() {
		CompletableFuture<Optional<Object>> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS).asyncExec();
		assertWhen(exec::get).throwException(exceptionMessage(containsString("TEST")));
	}
	
	@Test
	public void tesIgnoreExceptionThenAbsent() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).ignoreException().expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(false);
	}
}
