package com.vexsoftware.ajdbc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.vexsoftware.ajdbc.util.DatabaseExecution;
import com.vexsoftware.ajdbc.util.DatabaseFutureState;

/**
 * Represents the current state of a database related task, and allows for the
 * handling of completion and errors of said task.
 * 
 * <p>
 * <b>Note that the desired database operation will not begin until the {@link
 * execute()} method is called</b>. All {@link DatabaseCompletionHandler}
 * objects must be added before the {@code execute()} method is called. This is
 * to ensure that the operation does not complete asynchronously before the
 * completion handlers could be added.
 * </p>
 * 
 * @author Blake Beaupain
 * 
 * @param <V>
 *            The type of object returned upon database task completion
 */
public class DatabaseFuture<V> {

	/** The database task. */
	private final DatabaseTask<V> task;

	/** The current state. */
	private DatabaseFutureState state = DatabaseFutureState.CREATED;

	/** A concurrent-safe lock-free queue of completion handlers. */
	private Queue<DatabaseCompletionHandler<V>> completionHandlers = new ConcurrentLinkedQueue<DatabaseCompletionHandler<V>>();

	/**
	 * Instantiates a new {@code DatabaseFuture}.
	 * 
	 * @param task
	 *            The database task associated with this future
	 */
	public DatabaseFuture(DatabaseTask<V> task) {
		this.task = task;
		task.setFuture(this);
	}

	/**
	 * Begins execution of the database operation. Note that after this method
	 * is called, completion handlers can no longer be added.
	 * 
	 * @return This object for call chaining
	 */
	public DatabaseFuture<V> execute() {
		state = DatabaseFutureState.IN_PROGRESS;
		DatabaseExecution.getExecutorService().execute(task);
		return this;
	}

	/**
	 * Adds a {@code DatabaseCompletionHandler} to be called upon completion or
	 * exception of the database task represented by this future.
	 * 
	 * @param completionHandler
	 *            The completion handler
	 * @throws IllegalStateException
	 *             If the {@code execute()} method has already been called
	 * @return This object for call chaining
	 */
	public DatabaseFuture<V> addCompletionHandler(DatabaseCompletionHandler<V> completionHandler) {
		if (state != DatabaseFutureState.CREATED) {
			throw new IllegalStateException("Cannot add completion handlers for an initiated database operation");
		}
		completionHandlers.add(completionHandler);
		return this;
	}

	/**
	 * Removes a {@code DatabaseCompletionHandler} from the future.
	 * 
	 * @param completionHandler
	 *            The completion handler
	 * @throws IllegalStateException
	 *             If the {@code execute()} method has already been called
	 * @return This object for call chaining
	 */
	public DatabaseFuture<V> removeCompletionHandler(DatabaseCompletionHandler<V> completionHandler) {
		if (state != DatabaseFutureState.CREATED) {
			throw new IllegalStateException("Cannot remove completion handlers from an initiated database operation");
		}
		completionHandlers.remove(completionHandler);
		return this;
	}

	/**
	 * Broadcasts the task completion to all registered completion handlers.
	 * 
	 * @param result
	 *            The result
	 */
	protected void broadcastCompletion(V result) {
		state = DatabaseFutureState.COMPLETED;
		for (DatabaseCompletionHandler<V> completionHandler : completionHandlers) {
			completionHandler.onComplete(this, result);
		}
	}

	/**
	 * Broadcasts the task exception to all registered completion handlers.
	 * 
	 * @param cause
	 *            The result
	 */
	protected void broadcastException(Throwable cause) {
		state = DatabaseFutureState.EXCEPTION_CAUGHT;
		for (DatabaseCompletionHandler<V> completionHandler : completionHandlers) {
			completionHandler.onException(this, cause);
		}
	}

	/**
	 * Gets the state.
	 * 
	 * @return The state
	 */
	public DatabaseFutureState getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            The state
	 */
	protected void setState(DatabaseFutureState state) {
		this.state = state;
	}

}
