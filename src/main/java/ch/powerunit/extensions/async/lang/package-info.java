//@formatter:off
/**
 * This this the main package of the powerunit-extensions-async library that
 * exposes easy async test operation.
 * <p>
 * The framework exposes DSL to do repeated test of condition (possibly in
 * another thread), for example to wait of an asynchronously result. The 
 * builder mainly returns {@link java.util.concurrent.CompletableFuture}
 * and let the caller handle it, but also provides some <i>shortcut</i>
 * methods to directly retrieve the result.
 * <h1>Basic usage</h1>
 * 
 * <pre>
 * {@code
 * Optional<MyResultClass> result = WaitResult.
 *   of(MyCallable).
 *   expecting(MyPredicate).
 *   repeat(2).
 *   every(2,TimeUnit.MILLISECONDS).
 *   get();
 * }
 * </pre>
 * This sample defines an execution of maximal 2 retry, waiting 2 ms 
 * between the retry and return the result of the execution. The 
 * {@link java.util.Optional} will be present if a result is accepted by 
 * the predicate. In case of error during the execution of the 
 * {@link java.util.concurrent.Callable} an {@link java.lang.AssertionError}
 * will be thrown. Everything will be executed in this thread.
 * <p>
 * The sequence of actions will be :
 * <ol>
 * <li>Execution of the Callable. In case of Exception, an AssertionError 
 * is thrown (the actions are terminated).</li>
 * <li>Verify of the result of the execution is accepted by the Predicate.
 * If this is the case, return it as an Optional (the actions are terminated).
 * </li>
 * <li>Wait for 2 ms by sleeping the thread</li>
 * <li>Execution of the Callable. In case of Exception, an AssertionError 
 * is thrown (the actions are terminated).</li>
 * <li>Verify of the result of the execution is accepted by the Predicate.
 * If this is the case, return it as an Optional (the actions are terminated).
 * </li>
 * <li>Finally as no result were accepted, return an empty Optional.</li>
 * </ol>
 * 
 * <h1>Options</h1>
 * It is also possible to specify with the DSL, if error must be ignored or not, 
 * and which executor must be used for the asynchronous operations.
 * 
 * @since 1.0.0 Before this version, the main package was
 *        ch.powerunit.extensions.async ; Also starting from version 1.0.0 this
 *        library doesn't support version before Java 9.
 * @see ch.powerunit.extensions.async.lang.WaitResult WaitResult which is the
 *      main entry point of this library.
 * @see java.util.concurrent.CompletableFuture
 */
package ch.powerunit.extensions.async.lang;
//@formatter:on