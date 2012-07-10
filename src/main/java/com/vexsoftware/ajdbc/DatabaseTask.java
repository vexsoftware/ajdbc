package com.vexsoftware.ajdbc;

/**
 * An abstract task representing a database operation for internal use only.
 * 
 * @author Blake Beaupain
 * 
 * @param <V>
 *            The type of object returned by this operation
 */
public abstract class DatabaseTask<V> implements Runnable {

	/** The database future. */
	private DatabaseFuture<V> future;

	/**
	 * Executes the task.
	 * 
	 * @return The result of the task
	 * @throws Throwable
	 *             If an error occurs
	 */
	protected abstract V execute() throws Throwable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			V result = execute();
			future.broadcastCompletion(result);
		} catch (Throwable t) {
			future.broadcastException(t);
		}
	}

	/**
	 * Gets the database future.
	 * 
	 * @return The future
	 */
	public DatabaseFuture<V> getFuture() {
		return future;
	}

	/**
	 * Sets the database future.
	 * 
	 * @param future
	 *            The future
	 */
	protected void setFuture(DatabaseFuture<V> future) {
		this.future = future;
	}

}
