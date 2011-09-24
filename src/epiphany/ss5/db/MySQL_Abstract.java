package epiphany.ss5.db;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Abstract class that represents an SQL connection with a default query 
 * statement
 * @author Jeffrey Lin
 *
 */
public abstract class MySQL_Abstract implements DB_CONSTANTS{

	//
	// Variables to be inherited
	//
	
	/** The MySQL connection */
	protected java.sql.Connection con;
	
	/** Default template query */
	protected java.sql.PreparedStatement ps;
	
	
	//
	//Constructor
	//
	
	/** Represents an SQL connection with an empty default query statement */
	public MySQL_Abstract(){
		initCon();
	}
	
	/**
	 * Represents an SQL connection with a default query statement
	 * @param preparedStatementString	Default query statement
	 */
	public MySQL_Abstract(String preparedStatementString){
		initCon();
		initPS(preparedStatementString);
	}
	
	
	//
	// Methods to be inherited
	//
	
	/** Closes our MySQL connection */
	public void close(){
		try {con.close();} catch (SQLException e) {e.printStackTrace();}
	}
	
	/** Initializes statement and connection global variables */
	protected void initCon() {
		try {
			// Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(SERVER_URL, SERVER_USERNAME,
					SERVER_PASSWORD);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	 * Initializes our default MySQL query
	 * @param preparedStatementString	Default MySQL query
	 */
	protected void initPS(String preparedStatementString){
		try {
			ps = 	con.prepareStatement(
						preparedStatementString,
						Statement.RETURN_GENERATED_KEYS
					);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
}
