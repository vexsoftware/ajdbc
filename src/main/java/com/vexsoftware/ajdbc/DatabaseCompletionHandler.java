package com.vexsoftware.ajdbc;

/**
 * An interface used to handle the completion of a database related operation,
 * or an exception that was caught during the execution of the operation.
 * 
 * @author Blake Beaupain
 * 
 * @param <V>
 *            The type of object returned by the task
 */
public interface DatabaseCompletionHandler<V> {

	/**
	 * Called when the database operation completes successfully.
	 * 
	 * @param future
	 *            The database future representing the operation
	 * @param result
	 *            The result returned by the database operation
	 */
	public void onComplete(DatabaseFuture<V> future, V result);

	/**
	 * Called when the database operation encounters an exception.
	 * 
	 * @param future
	 *            The database future representing the operation
	 * @param cause
	 *            The cause of the exception
	 */
	public void onException(DatabaseFuture<V> future, Throwable cause);

}
