package epiphany.ss5.db;

/**
 * Methods for table maintenance and upkeep
 * @author Jeffrey Lin
 *
 */
public interface SS5TableManager {
	
	/** Remove server entries in the cache that have expired */
	public void removeOldServerEntries();
	/** Remove file entries in the cache that have expired */
	public void removeOldFileEntries();
	/** Do optimizations for lookup speed */
	public void optimizeTables();
	/** 
	 * Update folder sizes - folder sizes should be the sum of children folder
	 * sizes as well as files
	 */
	public void updateFolderSizes();
	/** Close the database connection */
	public void close();
}
