package epiphany.ss5.db;

import java.sql.SQLException;
import epiphany.ss5.objects.SS5Server;

/**
 * Class to store Samba server entries to the MySQL database
 * @author Jeffrey Lin
 *
 */
public class MySQL_ServerStorer extends MySQL_Abstract implements SS5ServerStorer{
	
	//
	//PUBLIC METHODS
	//
	
	/** Stores Samba server entries to the MySQL database */
	public MySQL_ServerStorer() {
		super(SS5SERVER_PREPARED_STATEMENT);
	}

	/** Store a server in the MySQL database */
	public void storeServer(SS5Server s) {
		_storeServer(s);
	}

	
	//
	//PROTECTED METHODS
	//

	/** Template MySQL query to insert a Samba server entry */
	protected static final String SS5SERVER_PREPARED_STATEMENT =
		"INSERT INTO  `"+SERVER_DATABASE+"`.`"+SERVERS_TABLE+"` \n" +
		"SET \n" +
		"`id` = null, \n" +
		"`netbios_name` = ?, \n" +
		"`ip` = ?, \n" +
		"`mac` = ?, \n" +
		"`index_date` = ? \n" +
		"ON DUPLICATE KEY UPDATE \n" +
		"`id` = LAST_INSERT_ID(id), \n" +
		"`netbios_name` = values(netbios_name), \n" +
		"`ip` = values(ip), \n" +
		"`mac` = values(mac), \n" +
		"`index_date` = values(index_date) \n" +
		""
	;
	
	/**
	 * Method that does the heavy lifting to store our server entry in MySQL
	 * @param s	Server object to be stored
	 */
	protected void _storeServer(SS5Server s){
		try {
			ps.setString	( 1, s.getNetbiosName()						);
			ps.setString	( 2, s.getIP()								);
			ps.setString	( 3, s.getMAC()								);
			ps.setDate		( 4, new java.sql.Date(s.getIndexDate())	);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("ERROR INSERTING Server: \n"+ps);
			e.printStackTrace();
		}
	}
}


///////////////////////////////////////////////////////////////////////////////	
////TABLE SCHEMA:
///////////////////////////////////////////////////////////////////////////////
//CREATE  TABLE  `ss5`.`servers` (  `id` smallint( 5  )  unsigned NOT  NULL  auto_increment ,
//	 `netbios_name` varchar( 16  )  NOT  NULL ,
//	 `ip` varchar( 15  )  NOT  NULL ,
//	 `mac` varchar( 12  )  NOT  NULL ,
//	 `index_date` date NOT  NULL ,
//	 PRIMARY  KEY (  `id`  ) ,
//	 UNIQUE KEY  `netbios_name` (  `netbios_name`  ) ,
//	 KEY  `ip` (  `ip`  ) ,
//	 KEY  `index_date` (  `index_date`  )  
//) 
//ENGINE  =  MyISAM  DEFAULT CHARSET  = latin1;
///////////////////////////////////////////////////////////////////////////////	
////END TABLE SCHEMA
///////////////////////////////////////////////////////////////////////////////