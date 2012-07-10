package com.vexsoftware.ajdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vexsoftware.ajdbc.factory.DatabaseConnectionFactory;
import com.vexsoftware.ajdbc.wrapper.AsynchronousConnection;

/**
 * Tests AJDBC with the PostgreSQL driver.
 * 
 * @author Blake Beaupain
 */
public class PostgreSQLTest {

	/** The connection. */
	private AsynchronousConnection connection;

	@Before
	public void setup() throws ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
	}

	@Test
	public void testConnection() {
		final CountDownLatch latch = new CountDownLatch(1);
		String user = "testuser";
		String pass = "testpass";
		String url = "jdbc:postgresql://localhost/testdb";

		DatabaseFuture<AsynchronousConnection> df = DatabaseConnectionFactory.getConnection(url, user, pass);
		df.addCompletionHandler(new DatabaseCompletionHandler<AsynchronousConnection>() {

			public void onComplete(DatabaseFuture<AsynchronousConnection> future, AsynchronousConnection result) {
				try {
					connection = result;
					Assert.assertTrue(!result.getConnection().isClosed());
				} catch (Exception ex) {
					Assert.fail(ex.getMessage());
				} finally {
					latch.countDown();
				}
			}

			public void onException(DatabaseFuture<AsynchronousConnection> future, Throwable cause) {
				try {
					Assert.fail(cause.getMessage());
				} finally {
					latch.countDown();
				}
			}

		}).execute();

		try {
			latch.await();
		} catch (InterruptedException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void testQuery() throws SQLException {
		testConnection();
		Assert.assertTrue(connection != null);
		Assert.assertTrue(!connection.getConnection().isClosed());

		final CountDownLatch latch = new CountDownLatch(1);

		connection.createStatement().executeQuery("SELECT * from testtable").addCompletionHandler(new DatabaseCompletionHandler<ResultSet>() {

			public void onComplete(DatabaseFuture<ResultSet> future, ResultSet result) {
				try {
					result.next();
					Assert.assertTrue(result.getString(1).equals("bar"));
				} catch (Exception ex) {
					Assert.fail(ex.getMessage());
				} finally {
					latch.countDown();
				}
			}

			public void onException(DatabaseFuture<ResultSet> future, Throwable cause) {
				try {
					Assert.fail(cause.getMessage());
				} finally {
					latch.countDown();
				}
			}

		}).execute();

		try {
			latch.await();
		} catch (InterruptedException ex) {
			Assert.fail(ex.getMessage());
		}
	}

}
