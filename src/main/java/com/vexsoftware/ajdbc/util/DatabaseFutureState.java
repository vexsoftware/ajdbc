package com.vexsoftware.ajdbc.util;

import com.vexsoftware.ajdbc.DatabaseFuture;

/**
 * Represents the various possible states of a {@link DatabaseFuture}.
 * 
 * @author Blake Beaupain
 */
public enum DatabaseFutureState {

	/** The database task has been created. */
	CREATED,

	/** The database task is in progress. */
	IN_PROGRESS,

	/** The database task has completed successfully. */
	COMPLETED,

	/** The database task encountered an exception. */
	EXCEPTION_CAUGHT

}
