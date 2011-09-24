package epiphany.ss5.db;

import java.sql.Statement;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Class with methods for table maintenance and upkeep
 * @author Jeffrey Lin
 *
 */
public class MySQL_TableManager extends MySQL_FolderSizer implements SS5TableManager{
	
	//
	//Required Public Methods
	//
	
	/** Optimizes tables using MySQL's built-in "OPTIMIZE TABLE" query */
	public void optimizeTables() {
		try {
			Statement stmt = con.createStatement();
			stmt.execute("OPTIMIZE TABLE "+SERVERS_TABLE+", "+FILES_TABLE);
		} catch (SQLException e) {e.printStackTrace();}
	}

	/**	Removes server entries that are older than the cache expiration date */
	public void removeOldServerEntries() {
		try {
			Statement stmt = con.createStatement();
			stmt.execute(
				"DELETE FROM `"+SERVERS_TABLE+"` " +
				"WHERE `index_date` < '" + 
				getDateDaysAgo(ENTRY_EXPIRATION_AGE) +	"'"
			);
		} catch (SQLException e) {e.printStackTrace();}
	}

	/** Removes file entries that are older than the cache expiration date */
	public void removeOldFileEntries() {
		try {
			Statement stmt = con.createStatement();
			stmt.execute(
				"DELETE FROM `"+FILES_TABLE+"` " +
				"WHERE `index_date` < '" + 
				getDateDaysAgo(ENTRY_EXPIRATION_AGE) +	"'"
			);
		} catch (SQLException e) {e.printStackTrace();}
	}

	
	//
	//Private Methods
	//
	
	/** Convenience method to get the SQL Date that represents __ days ago
	 * Doesn't have to be super exact, so using Java's time is acceptable
	 * because it should never be more than a couple seconds off from MySQL
	 * 
	 * TODO: Use MySQL's time instead
	 * 
	 * @param daysAgo	Number of days ago
	 * @return	SQL date representation of __ days ago
	 */
	private java.sql.Date getDateDaysAgo(int daysAgo) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -daysAgo);
		return (new java.sql.Date(cal.getTime().getTime()));
	}
	
}
