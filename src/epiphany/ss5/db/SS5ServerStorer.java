package epiphany.ss5.db;

import epiphany.ss5.objects.SS5Server;

/**
 * Methods required to store server entries in a database
 * @author Jeffrey Lin
 *
 */
public interface SS5ServerStorer {
	
	/**
	 * Method to store a server object in a database
	 * @param s	Server object to be stored
	 */
	public void storeServer(SS5Server s);
	
	/** Closes our database connection */
	public void close();
}
