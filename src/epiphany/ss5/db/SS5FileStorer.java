package epiphany.ss5.db;

import epiphany.ss5.objects.SS5File;

/**
 * Methods required to store file entries in a database
 * @author Jeffrey Lin
 *
 */
public interface SS5FileStorer {
	
	/**
	 * Method to store a file object in a database
	 * @param f	File object to be stored
	 * @return	Inserted row ID
	 */
	public int storeFile(SS5File f);	//returns last_inserted_id
	
	/** Closes our database connection */
	public void close();
}
