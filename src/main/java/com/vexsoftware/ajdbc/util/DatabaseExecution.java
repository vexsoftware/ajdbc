package com.vexsoftware.ajdbc.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Static execution environment for database operations. The default
 * {@link ExecutorService}, if none was specified, is the one returned from
 * {@link Executors.newSingleThreadExecutor()} and will use one thread for all
 * database operations.
 * 
 * @author Blake Beaupain
 */
public class DatabaseExecution {

	/** The executor service. */
	private static ExecutorService executorService;

	/**
	 * Gets the executor service.
	 * 
	 * @return The executor service
	 */
	public static ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = Executors.newSingleThreadExecutor();
		}
		return executorService;
	}

	/**
	 * Sets the executor service.
	 * 
	 * @param executorService
	 *            The executor service
	 */
	public static void setExecutorService(ExecutorService executorService) {
		DatabaseExecution.executorService = executorService;
	}

}
