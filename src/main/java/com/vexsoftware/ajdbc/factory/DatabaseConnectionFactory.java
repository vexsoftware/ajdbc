package com.vexsoftware.ajdbc.factory;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.vexsoftware.ajdbc.DatabaseFuture;
import com.vexsoftware.ajdbc.DatabaseTask;
import com.vexsoftware.ajdbc.wrapper.AsynchronousConnection;

/**
 * <p>
 * A static factory utility class that is used to create database connections
 * asynchronously. Connection attempts can be made with the static
 * {@code getConnection} methods provided in this class, but please note that
 * the driver needs to be loaded with {@code Class.forName("driver class")}
 * method. Note that any connections will not actually be opened until the
 * {@link execute()} method is called on the {@link DatabaseFuture} returned by
 * these methods.
 * </p>
 * 
 * <p>
 * Please take note that the {@code getConnection} methods in this class return
 * <b>immediately without blocking</b>, and it is not guaranteed (and almost
 * certainly not the case) that the connection has been successfully established
 * at the time of method returning. To handle the completion or failure of a
 * database connection attempt, the {@code DatabaseFuture} returned by these
 * methods should be used to either wait on the connection to finish or to
 * register a {@link DatabaseCompletionHandler} for asynchronous callback.
 * </p>
 * 
 * @author Blake Beaupain
 */
public class DatabaseConnectionFactory {

	/**
	 * Attempts to establish a connection to the given database URL. Returns a
	 * {@code DatabaseFuture<Connection>} that can be used to handle the
	 * completion of this operation.
	 * 
	 * @param url
	 *            A database URL in the form of {@code jdbc:subprotocol:subname}
	 *            with a user and password specified, if necessary
	 * @return A {@code DatabaseFuture<Connection>} taht can be used to handle
	 *         the completion of this connection attempt
	 */
	public static DatabaseFuture<AsynchronousConnection> getConnection(final String url) {
		return new DatabaseFuture<AsynchronousConnection>(new DatabaseTask<AsynchronousConnection>() {
			@Override
			protected AsynchronousConnection execute() throws SQLException {
				return new AsynchronousConnection(DriverManager.getConnection(url));
			}
		});
	}

	/**
	 * Attempts to establish a connection to the given database URL with the
	 * specified user and password. Returns a {@code DatabaseFuture<Connection>}
	 * that can be used to handle the completion of this operation.
	 * 
	 * @param url
	 *            A database URL in the form of {@code jdbc:subprotocol:subname}
	 * @param user
	 *            The database user
	 * @param password
	 *            The user's password
	 * @return A {@code DatabaseFuture<Connection>} that can be used to handle
	 *         the completion of this connection attempt
	 */
	public static DatabaseFuture<AsynchronousConnection> getConnection(final String url, final String user, final String password) {
		return new DatabaseFuture<AsynchronousConnection>(new DatabaseTask<AsynchronousConnection>() {
			@Override
			protected AsynchronousConnection execute() throws SQLException {
				return new AsynchronousConnection(DriverManager.getConnection(url, user, password));
			}
		});
	}

}
