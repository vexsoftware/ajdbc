package com.vexsoftware.ajdbc.wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vexsoftware.ajdbc.DatabaseFuture;
import com.vexsoftware.ajdbc.DatabaseTask;

/**
 * Holds a {@link Statement} reference and provides methods for asynchronous
 * statement operations. Note that the methods in this class simply wrap the
 * similar methods in {@code Statement} and execute them asynchronously - the
 * behavior and returned objects do not differ.
 * 
 * @author Blake Beaupain
 */
public class AsynchronousStatement {

	/** The statement. */
	private final Statement statement;

	/**
	 * Instantiates a new {@code AsynchronousStatement}.
	 * 
	 * @param statement
	 *            The statement
	 */
	public AsynchronousStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * Executes the given SQL statement asynchronously, which may return
	 * multiple results.
	 * 
	 * @param sql
	 *            The SQL statement
	 * @return A database future representing this operation
	 */
	public DatabaseFuture<Boolean> execute(final String sql) {
		return new DatabaseFuture<Boolean>(new DatabaseTask<Boolean>() {
			@Override
			protected Boolean execute() throws SQLException {
				return statement.execute(sql);
			}
		});
	}

	/**
	 * Executes the given SQL statement asynchronously, which returns a single
	 * {@code ResultSet} object.
	 * 
	 * @param sql
	 *            The SQL statement
	 * @return A database future representing this operation
	 */
	public DatabaseFuture<ResultSet> executeQuery(final String sql) {
		return new DatabaseFuture<ResultSet>(new DatabaseTask<ResultSet>() {
			@Override
			protected ResultSet execute() throws SQLException {
				return statement.executeQuery(sql);
			}
		});
	}

	/**
	 * Executes the given SQL statement, which may be an {@code INSERT},
	 * {@code UPDATE}, or {@code DELETE} statement or an SQL statement that
	 * returns nothing asynchronously.
	 * 
	 * @param sql
	 *            The SQL statement
	 * @return A database future representing this operation
	 */
	public DatabaseFuture<Integer> executeUpdate(final String sql) {
		return new DatabaseFuture<Integer>(new DatabaseTask<Integer>() {
			@Override
			protected Integer execute() throws SQLException {
				return statement.executeUpdate(sql);
			}
		});
	}

	/**
	 * Gets the {@code Statement} held by this object. Note that any methods
	 * called on this statement will be performed synchronously via the standard
	 * JDBC functionality.
	 * 
	 * @return The statement
	 */
	public Statement getStatement() {
		return statement;
	}

}
