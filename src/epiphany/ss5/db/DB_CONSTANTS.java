package epiphany.ss5.db;

/**
 * Interface that stores connection defaults
 * TODO: Implement command line arguments instead
 * @author Jeffrey Lin
 *
 */
public interface DB_CONSTANTS {
	//MySQL Connection Variables
	public static final int SERVER_PORT = 3306; // standard mysql port
	public static final String SERVER_NAME = "localhost";
	public static final String SERVER_DATABASE = "ss5";
	public static final String SERVER_USERNAME = "ss3indexer";
	public static final String SERVER_PASSWORD = "PASSWORD_HERE";
	public static final String SERVER_URL = 
		"jdbc:mysql://" + SERVER_NAME + ":" +
		SERVER_PORT + "/" + SERVER_DATABASE;
	
	// MySQL Tables
	public static final String FILES_TABLE 			= "files";
	public static final String SERVERS_TABLE		= "servers";
	
	// Cached entry expiration date in days
	public static final int ENTRY_EXPIRATION_AGE	= 2;	// in days	
}