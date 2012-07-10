package com.vexsoftware.ajdbc.wrapper;

import java.sql.Connection;
import java.sql.SQLException;

import com.vexsoftware.ajdbc.DatabaseFuture;
import com.vexsoftware.ajdbc.DatabaseTask;

/**
 * Holds a {@link Connection} and provides methods for asynchronous
 * connection-related operations.
 * 
 * @author Blake Beaupain
 */
public class AsynchronousConnection {

	/** The connection. */
	private final Connection connection;

	/**
	 * Instantiates a new {@code AsynchronousConnection}.
	 * 
	 * @param connection
	 *            The connection
	 */
	public AsynchronousConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Creates an asynchronous statement.
	 * 
	 * @return The asynchronous statement
	 */
	public AsynchronousStatement createStatement() throws SQLException {
		/*
		 * Note: I do not believe that the connection.createStatement() method
		 * performs any blocking, therefore we will execute it synchronously in
		 * this method.
		 */
		return new AsynchronousStatement(connection.createStatement());
	}

	/**
	 * Closes the connection asynchronously.
	 * 
	 * @return A future representing this operation
	 */
	public DatabaseFuture<Void> close() {
		return new DatabaseFuture<Void>(new DatabaseTask<Void>() {
			@Override
			protected Void execute() throws Throwable {
				connection.close();
				return null;
			}
		});
	}

	/**
	 * Prepares a statement asynchronously.
	 * 
	 * @param sql
	 *            The SQL query to prepare
	 * @return A future representing this operation
	 */
	public DatabaseFuture<AsynchronousPreparedStatement> prepareStatement(final String sql) {
		return new DatabaseFuture<AsynchronousPreparedStatement>(new DatabaseTask<AsynchronousPreparedStatement>() {
			@Override
			protected AsynchronousPreparedStatement execute() throws SQLException {
				return new AsynchronousPreparedStatement(connection.prepareStatement(sql));
			}
		});
	}

	/**
	 * Gets the connection.
	 * 
	 * @return The connection
	 */
	public Connection getConnection() {
		return connection;
	}

}
