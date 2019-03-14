package ch.powerunit.extensions.async.lang;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
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
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expectingNotNull().repeat(100)
				.everySecond().asyncExec();
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyOKExec() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<Object>> exec = WaitResult.on(new Object()).expectingNotNull().repeat(100)
				.everySecond().asyncExec(Executors.newFixedThreadPool(2));
		Optional<Object> result = exec.get();
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testObjectMethodDirectlyOKWithEquals() throws InterruptedException, ExecutionException {
		CompletableFuture<Optional<String>> exec = WaitResult.on("X")
				.expectingEqualsTo(new StringBuilder("X").toString()).repeat(100).asFastAsPossible().asyncExec();
		Optional<String> result = exec.get();
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
		}).dontIgnoreException().expecting(o -> true).repeatOnlyOnce().asyncExec();
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
				.every(10, TimeUnit.MILLISECONDS).finish(Executors.newCachedThreadPool());
		assertThat(result).isNotNull();
		assertThat(result.isPresent()).is(true);
	}

	@Test
	public void testNotIgnoreExceptionThenExceptionWithFinish() {
		WaitResultBuilder5<Object> exec = WaitResult.of(() -> {
			throw new IllegalArgumentException("TEST");
		}).expecting(o -> true).repeatTwice().every(10, TimeUnit.MILLISECONDS);
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
				.finishWithAResult(Executors.newSingleThreadExecutor());
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

	// Shortcut
	@Test
	public void testApply() throws InterruptedException, ExecutionException {
		assertThat(WaitResult.on(true).expecting(b -> b).repeatOnlyOnce().usingDefaultExecutor()
				.thenApply(o -> o.map(Object::toString).orElse("")).get()).is("true");
	}

	@Test
	public void testAccept() throws InterruptedException, ExecutionException {
		StringBuilder sb = new StringBuilder();
		WaitResult.on(true).expecting(b -> b).repeatOnlyOnce().usingDefaultExecutor().thenAccept(sb::append).get();
		assertThat(sb.toString()).equals("true");
	}

	// Shortcut
	@Test
	public void testJoin() {
		assertThat(WaitResult.on(true).expecting(b -> b).repeatOnlyOnce().usingDefaultExecutor().join())
				.is(optionalIsPresent());
	}

	@Test
	public void testJoinWithAResult() {
		assertThat(WaitResult.on(true).expecting(b -> b).repeatOnlyOnce().usingDefaultExecutor().joinWithAResult())
				.is(true);
	}

	@Test
	public void testJoinWithAResultMissing() {
		assertWhen(
				() -> WaitResult.on(false).expecting(b -> b).repeatOnlyOnce().usingDefaultExecutor().joinWithAResult())
						.throwException(instanceOf(AssertionError.class));
	}

	// On runnable
	@Test
	public void testOnRunnableNoError() {
		assertThat(WaitResult.ofRunnable(() -> {
		}).repeatOnlyOnce().asyncExec().join()).is(optionalIsPresent());
	}

	@Test
	public void testOnRunnableError() {
		assertThat(WaitResult.ofRunnable(() -> {
			throw new IllegalArgumentException();
		}).repeatOnlyOnce().asyncExec().join()).is(optionalIsNotPresent());
	}

	// Exception
	@Test
	public void testOnExeptionMissing() {
		assertThat(WaitResult.forException(() -> "x").repeatOnlyOnce().asyncExec().join()).is(optionalIsNotPresent());
	}

	@Test
	public void testOnExeptionNotMissing() {
		assertThat(WaitResult.forException(() -> {
			throw new IllegalArgumentException("x");
		}).repeatOnlyOnce().asyncExec().join()).is(optionalIsPresent());
	}

	// Map
	@Test
	public void testMapNotPresent() {
		assertThat(WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> false).repeatOnlyOnce()
				.map(s -> "s" + s).asyncExec().join()).is(optionalIsNotPresent());
	}

	@Test
	public void testMapPresent() {
		assertThat(WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> true).repeatOnlyOnce()
				.map(s -> "s" + s).asyncExec().join()).is(optionalIs("sx"));
	}

	// Filter
	@Test
	public void testFilter() {
		assertThat(WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> true).repeatOnlyOnce()
				.map(s -> "s" + s).filter(x -> false).asyncExec().join()).is(optionalIsNotPresent());
	}

	// or timeout
	@Test
	public void testOrTimeout() {
		assertWhen(() -> WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> false).repeat(10).everyMinute()
				.usingDefaultExecutor().orTimeout(2, TimeUnit.SECONDS).get())
						.throwException(instanceOf(ExecutionException.class));
	}

	// minimalCompletionStage
	@Test
	public void testMinimalCompletionStage() {
		assertThat(WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> true).repeatOnlyOnce()
				.usingDefaultExecutor().minimalCompletionStage​().toCompletableFuture().join()).is(optionalIs("x"));
	}

	// Final get
	@Test
	public void testFinalGet() throws InterruptedException, ExecutionException {
		assertThat(WaitResult.of(() -> "x").dontIgnoreException().expecting(s -> true).repeatOnlyOnce()
				.usingDefaultExecutor().get()).is(optionalIs("x"));
	}

}
