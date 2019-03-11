/**
 * 
 */
package ch.powerunit.extensions.async.impl;

/**
 * @author borettim
 *
 */
class ExceptionHandler {// package protected
	private final boolean ignoreException;

	private final boolean alsoDontThrowLastExceptionWhenNoResult;

	public ExceptionHandler(boolean ignoreException, boolean alsoDontThrowLastExceptionWhenNoResult) {
		this.ignoreException = ignoreException;
		this.alsoDontThrowLastExceptionWhenNoResult = alsoDontThrowLastExceptionWhenNoResult;
	}

	public void handleException(Exception e) {
		if (e != null && !ignoreException) {
			throw new AssertionError("Unable to obtains the result during one try, because of " + e.getMessage()
					+ " ; Original error class is " + e.getClass(), e);
		}
	}

	public void handleFinalException(Exception e) {
		if (e != null && !alsoDontThrowLastExceptionWhenNoResult) {
			throw new AssertionError("Unable to obtains the result and finish in error, because of " + e.getMessage()
					+ " ; Original error class is " + e.getClass(), e);
		}
	}
}
