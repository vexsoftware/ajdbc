## AJDBC
Hey there! We're Vex Software, and thanks for checking our project out. AJDBC (**A**synchronous **J**ava **D**ata**B**ase **C**onnectivity) is a lightweight, simple, and easy to use wrapper library that adds asynchronous functionality to JDBC. It's also a mouthful to pronounce! AJDBC has no dependencies except JRE 1.5, and works with anything that JDBC works with. AJDBC has been tested and works, but has not yet been tested in a production environment, so we're sorry if you encounter some problems. If you find any bugs, please report them on our [issue page](https://github.com/vexsoftware/ajdbc/issues).

### Why AJDBC?
In high performance production environments, SQL is often used for storage. Java comes with a decent but relatively rudimentary solution for this - JDBC. Unfortunately, JDBC is _old_, _outdated_, and simply _not up to par_ in terms of performance and flexible design. At Vex Software, we often found ourselves designing asynchronous callback multi-threaded systems from the ground up for many of our projects to simply maintain decent non-blocking server performance. We were simply surprised to find that, after all these years, the Java community still does not have a functional but simple asynchronous wrapper for the JDBC API. No bueno. We set out to solve this issue with AJDBC.

### Limitations
Right now, AJDBC can handle the basics - SQL connections, queries, result sets, and prepared statements. We simply don't have the time to implement every single feature that JDBC has to offer right now. Don't worry though - AJDBC provides easy ways for you to seamlessly work with JDBC at the same time although in a synchronous context. If you really would like a specific feature implemented, go ahead and [add an issue](https://github.com/vexsoftware/ajdbc/issues) on our Github project page.

Here's the only catch though: you have to add your ```DatabaseCompletionHandler``` objects _before_ calling ```execute()```, and your queries will not complete (or even begin) until ```execute()``` is called. This ensures that there will be no spooky unexpected behavior that occurs, such as the SQL operation finishing in a separate thread before the completion handler even gets added!

### Code Examples
We're programmers ourselves, and we know what programmers like to see when checking out a badass new tool to use - code examples. Enough with the paragraphs of English, it's time for some _Java_ baby. We'll cut right to the chase and show you how simple AJDBC is to use.

#### Opening a connection asynchronously
Here's how you can open a connection to a database completely asynchronously. Aw yeah.
```java
// Load up your JDBC driver like you normally would.
Class.forName("com.mysql.jdbc.Driver");

// Create our DatabaseFuture object (url is a normal JDBC URL).
DatabaseFuture<AsynchronousConnection> future = DatabaseConnectionFactory.openConnection(url);

// Attach a completion handler to our future.
future.addCompletionHandler(new DatabaseCompletionHandler<AsynchronousConnection>() {
    public void onComplete(DatabaseFuture<AsynchronousConnection> future, AsynchronousConnection c) {
        // Our connection has successfully been opened.
    }

    public void onException(DatabaseFuture<AsynchronousConnection> future, Throwable cause) {
        // Uh oh! Unable to open the connection.
    }
});

// Finally, begin asynchronous execution of our SQL operation.
future.execute(); // Returns immediately.
```

#### Executing a query and getting results
So now we've opened a connection asynchronously and have a reference to it called ```c```. Now we'll show you how to whip out some badass code to execute a query totally asynchronously for that connection and obtain a result set.
```java

// Create an asynchronous statement.
AsynchronousStatement stmt = c.createStatement();

// Initialize the query and obtain the future.
DatabaseFuture<ResultSet> future = stmt.executeQuery("SELECT * FROM foo WHERE bar = 'foobar'");

// Attach a completion handler to the future.
future.addCompletionHandler(new DatabaseCompletionHandler<ResultSet>() {
    public void onComplete(DatabaseFuture<ResultSet> future, ResultSet results) {
        // We have a result set, parse it and handle it.
    }

    public void onException(DatabaseFuture<ResultSet> future, Throwable cause) {
        // Uh oh, something bad happened.
    }
});

// Finally, begin asynchronous execution of the operation.
future.execute();
```

### Shortcuts and call-chaining
We know how verbose Java can get. It's often _painful_ and sometimes downright _excruciating_. Borderline ridiculous. That's why we created some shortcuts for you. That's right, you can chain your AJDBC method calls to streamline and simplify the task at hand. For instance, you can chain these calls into one line of code:
```java
DatabaseConnectionFactory.openConnection(url).addCompletionHandler(handler).execute();
```
Yeah it's a long line of code, but that's the best you're going to get considering the amount of work that is done in the background. In fact, almost every method in ```DatabaseFuture``` will return its' own reference so you can whip out some mad chain-calling code to your hearts' content. We've got your back. Just don't get too carried away!

### How does it work under the hood?
It's pretty simple. We dispatch database tasks to a static ```ExecutorService``` instance held in ```com.vexsoftware.ajdbc.util.DatabaseExecution``` where the work is done for you and either completes successfully or fails completely. Either way, they broadcast what happened to your ```DatabaseFuture``` which then notifies the ```DatabaseCompletionHandler```s you added to it. We know that introducing a static global state to a program is bad, especially for unit testing, but we decided to aim for a sanctuary of simplicity in the insane world of asynchronous I/O.

By default for safety purposes, AJDBC will only use a single-threaded executor service for asynchronous database task handling. You can rest assured that, even though you wrote your code with absolutely zero concern for thread safety, AJDBC has you covered. What's that you ask? You want to know if you can you customize which executor service to use? Hell yes you can brother. Like I said, we've got you covered.

 If your code is safe to run in a concurrent environment and you're feeling frisky enough though, you can even use a thread pool like so:
```java
ExecutorService threadPool = Executors.newFixedThreadPool(4);
DatabaseExecution.setExecutorService(threadPool);
```
With this setup, any SQL tasks you submit will run in parallel. Just make sure your code can handle it without introducing race conditions. Simplicity and stability take precedence over high performance code that breaks all the time.

### Fallback to synchronous JDBC
Did we not implement a feature that you need? Sorry about that, man. Here's how your code can easily fallback to icky synchronous JDBC operations. Remember that you can always create an issue on our Github project page and ask us for some new features!

Let's assume we have an ```AsynchronousConnection``` whose reference is named ```ajdbcConnection``` and we need to do some stuff that JDBC can do but AJDBC can't. We simply call the ```getConnection()``` method, which returns a simple JDBC connection, and use it however we need to.
```java
Connection jdbcConnection = ajdbcConnection.getConnection();
jdbcConnection.doSomething(); // Done synchronously, but whatever.
```

That's all there is to it! Thanks again for checking AJDBC out.