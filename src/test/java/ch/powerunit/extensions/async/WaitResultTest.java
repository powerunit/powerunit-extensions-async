package ch.powerunit.extensions.async;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import ch.powerunit.Test;
import ch.powerunit.TestSuite;
import ch.powerunit.extensions.async.lang.WaitResultBuilder5;

public class WaitResultTest implements TestSuite {

	private static class Handler {
		public boolean ok = false;
		public int count = 0;
	}

	// async

	@Test
	public void testObjectMethodDirectlyOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expecting(o -> true).repeat(100)
				.everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodNotDirectlyOK() throws InterruptedException, ExecutionException {
		Handler h = new Handler();
		CompletableFuture<Optional<Handler>> exec = WaitResult.on(h).expecting(o -> o.ok).repeat(10)
				.every(Duration.ofMillis(1000)).asyncExec();
		Thread.sleep(2000);
		h.ok = true;
		Optional<Handler> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
		assertThat(result.get().ok).is(true);
	}

	@Test
	public void testNotIgnoreExceptionFirstThenException() {
		CompletableFuture<Optional<Object>> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).expecting(o -> true).repeatOnlyOnce().asyncExec();
		assertWhen(exec::get).throwException(exceptionMessage(containsString("TEST")));
	}

	@Test
	public void testNotIgnoreExceptionLastThenException() {
		Handler h = new Handler();
		CompletableFuture<Optional<Handler>> exec = WaitResult.<Handler>of(() -> {
			if (h.count >= 2) {
				throw new IllegalArgumentException("TEST");
			}
			h.count++;
			return h;
		}).ignoreException(false).expecting(o -> o.ok).repeat(10).every(10, TimeUnit.MILLISECONDS).asyncExec();
		assertWhen(exec::get).throwException(exceptionMessage(containsString("TEST")));
	}

	@Test
	public void testIgnoreExceptionThenAbsent() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).ignoreException().expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(false);
	}

	// finish

	@Test
	public void testObjectMethodDirectlyOKWithFinish() {
		Optional<Object> result = WaitResult.on(new Object()).expecting(o -> true).repeat(100)
				.every(10, TimeUnit.MILLISECONDS).finish();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testNotIgnoreExceptionThenExceptionWithFinish() {
		WaitResultBuilder5<Object> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS);
		assertWhen((Callable<Optional<Object>>) exec::finish).throwException(exceptionMessage(containsString("TEST")));
	}

	@Test
	public void testIgnoreExceptionThenAbsentWithFinish() {
		Optional<Object> result = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).ignoreException().expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS).finish();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(false);
	}

	// finishWithAError

	@Test
	public void testObjectMethodDirectlyOKWithFinishWithAResult() {
		Object result = WaitResult.on(new Object()).expecting(o -> true).repeat(100).every(10, TimeUnit.MILLISECONDS)
				.finishWithAResult();
		assertThat(result).isNotNull();
	}

	@Test
	public void testNotIgnoreExceptionThenExceptionWithFinishAResult() {
		WaitResultBuilder5<Object> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS);
		assertWhen((Callable<Object>) exec::finishWithAResult).throwException(exceptionMessage(containsString("TEST")));
	}

	@Test
	public void testIgnoreExceptionThenFailureWithFinish() {
		WaitResultBuilder5<Object> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).ignoreException().expecting(o -> true).repeat(10).every(10, TimeUnit.MILLISECONDS);
		assertWhen((Callable<Object>) exec::finishWithAResult).throwException(instanceOf(AssertionError.class));
	}

	@Test
	public void testObjectMethodDirectlyOnConditionOKWithFinishWithAResult() {
		Object result = WaitResult.onCondition(() -> true).repeat(100).every(10, TimeUnit.MILLISECONDS)
				.finishWithAResult();
		assertThat(result).isNotNull();
	}

	// samethread
	@Test
	public void testObjectMethodDirectlyOKInSameThread() {
		Optional<Object> result = WaitResult.on(new Object()).expecting(o -> true).repeat(100)
				.every(10, TimeUnit.MILLISECONDS).get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	// Any / All / Not

	@Test
	public void testObjectMethodDirectlyWithNotIsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expectingNot(o -> false).repeat(100)
				.everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAny1IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expectingAnyOf(o -> false, o -> true)
				.repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAny2IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object())
				.expectingAnyOf(o -> false, o -> false, o -> true).repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAny3IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object())
				.expectingAnyOf(o -> false, o -> false, o -> false, o -> true).repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAll1IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expectingAllOf(o -> true, o -> true)
				.repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAll2IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object())
				.expectingAllOf(o -> true, o -> true, o -> true).repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyWithAll3IsOK() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object())
				.expectingAllOf(o -> true, o -> true, o -> true, o -> true).repeat(100).everyMs(10).asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	// Interrupt
	@Test
	public void testObjectMethodNotDirectlyOKThenInterrupt() throws InterruptedException, ExecutionException {
		Handler h = new Handler();
		CompletableFuture<Optional<Handler>> exec = WaitResult.on(h).expecting(o -> o.ok).repeat(10)
				.every(Duration.ofMillis(1000)).asyncExec();
		exec.cancel(true);
		assertWhen(exec::get).throwException(instanceOf(CancellationException.class));
	}

}
