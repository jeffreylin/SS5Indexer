package epiphany.ss5.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import epiphany.ss5.objects.SS5File;

/**
 * Class to store file entries in MySQL table
 * @author Jeffrey Lin
 *
 */
public class MySQL_FileStorer extends MySQL_Abstract implements SS5FileStorer{
	
	//
	//PUBLIC METHODS
	//
	
	/** Class to store file entries in MySQL table */
	public MySQL_FileStorer() {
		super(SS5FILE_PREPARED_STATEMENT);
	}
	
	/**
	 * Method that stores an SS5File in the MySQL database
	 * @return	Last inserted row ID
	 */
	public int storeFile(SS5File f){	//returns last_inserted_id
		return _storeFile(f);
	}
	
	
	//
	//PROTECTED METHODS
	//
	/** Template for our MySQL query to store an SS5File */
	protected static final String SS5FILE_PREPARED_STATEMENT =
		"INSERT INTO  `"+SERVER_DATABASE+"`.`"+FILES_TABLE+"` \n" +
		"SET \n" +
		"`id` = null, \n" +
		"`parent` = ?, \n" +
		"`url` = ?, \n" +
		"`size` = ?, \n" +
		"`last_modified` = ?, \n" +
		"`attr_readonly` = ?, \n" +
		"`attr_hidden` = ?, \n" +
		"`attr_system` = ?, \n" +
		"`attr_volume` = ?, \n" +
		"`attr_directory` = ?, \n" +
		"`attr_archive` = ?, \n" +
		"`type` = ?, \n" +
		"`index_date` = ? \n" +
		"ON DUPLICATE KEY UPDATE \n" +
		"`id` = LAST_INSERT_ID(id), \n" +
		"`parent` = values(parent), \n" +
		"`size` = values(size), \n" +
		"`last_modified` = values(last_modified), \n" +
		"`attr_readonly` = values(attr_readonly), \n" +
		"`attr_hidden` = values(attr_hidden), \n" +
		"`attr_system` = values(attr_system), \n" +
		"`attr_volume` = values(attr_volume), \n" +
		"`attr_directory` = values(attr_directory), \n" +
		"`attr_archive` = values(attr_archive), \n" +
		"`type` = values(type), \n" +
		"`index_date` = values(index_date)" +
		""
	;
	
	/**
	 * Method that does the heavy lifting for our MySQL query
	 * @param f	Entry to be stored
	 * @return	MySQL last insert ID
	 */
	protected int _storeFile(SS5File f){
		try {
			ps.setInt		( 1, f.getParent()							);
			ps.setString	( 2, f.getURL()								);
			ps.setLong		( 3, f.getSize()							);
			ps.setTimestamp	( 4, sanitizeDatetime(f.getLastModified())	);
			ps.setBoolean	( 5, f.getATTR_ReadOnly()					);
			ps.setBoolean	( 6, f.getATTR_Hidden()						);
			ps.setBoolean 	( 7, f.getATTR_System()						);
			ps.setBoolean 	( 8, f.getATTR_Volume()						);
			ps.setBoolean 	( 9, f.getATTR_Directory()					);
			ps.setBoolean	(10, f.getATTR_Archive()					);
			ps.setInt		(11, f.getType()							);
			ps.setDate		(12, new java.sql.Date(f.getIndexDate())	);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			int last_insert_id = rs.getInt(1);
			return last_insert_id;
		} catch (SQLException e) {
			System.out.println("ERROR INSERTING FILE: \n"+ps);
			e.printStackTrace();
		}
		return -1;	//something went wrong =[
	}
	
	
	//
	//PRIVATE METHODS
	//
	
	/**
	 * Sanitizes input for a MySQL Datetime field
	 * See: http://dev.mysql.com/doc/refman/5.0/en/datetime.html
	 * Max Datetime('9999-12-31 23:59:59') => Long(253402329599000L)
	 * @param l	UNIX-style timestamp
	 */
	protected java.sql.Timestamp sanitizeDatetime(long l){
		if(l > 253402329599000L){
			return new java.sql.Timestamp(253402329599000L);
		}
		else{
			return new java.sql.Timestamp(l);
		}
	}
}


///////////////////////////////////////////////////////////////////////////////	
////TABLE SCHEMA:
///////////////////////////////////////////////////////////////////////////////
//CREATE TABLE `ss5`.`files` (
//	`id` mediumint( 8 ) unsigned NOT NULL AUTO_INCREMENT ,
//	`parent` mediumint( 8 ) unsigned NOT NULL ,
//	`url` varchar( 512 ) NOT NULL ,
//	`size` bigint( 47 ) unsigned NOT NULL ,
//	`last_modified` datetime NOT NULL ,
//	`attr_readonly` tinyint( 1 ) NOT NULL ,
//	`attr_hidden` tinyint( 1 ) NOT NULL ,
//	`attr_system` tinyint( 1 ) NOT NULL ,
//	`attr_volume` tinyint( 1 ) NOT NULL ,
//	`attr_directory` tinyint( 1 ) NOT NULL ,
//	`attr_archive` tinyint( 1 ) NOT NULL ,
//	`type` mediumint( 7 ) unsigned NOT NULL ,
//	`index_date` date NOT NULL ,
//	PRIMARY KEY ( `id` ) ,
//	UNIQUE KEY `url` ( `url` ) ,
//	KEY `parent` ( `parent` ) ,
//	KEY `size` ( `size` ) ,
//	KEY `last_modified` ( `last_modified` ) ,
//	KEY `index_date` ( `index_date` ) 
//) 
//ENGINE = MYISAM DEFAULT CHARSET = latin1;
///////////////////////////////////////////////////////////////////////////////	
////END TABLE SCHEMA
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
////TABLE SCHEMA FOR TABLE IN MEMORY TABLE:
///////////////////////////////////////////////////////////////////////////////
//CREATE TABLE  `ss5`.`files` (
//		`id` MEDIUMINT( 8 ) UNSIGNED NOT NULL AUTO_INCREMENT ,
//		 `parent` MEDIUMINT( 8 ) UNSIGNED NOT NULL ,
//		 `url` VARCHAR( 410 ) NOT NULL ,
//		 `size` BIGINT( 47 ) UNSIGNED NOT NULL ,
//		 `last_modified` DATETIME NOT NULL ,
//		 `attr_readonly` TINYINT( 1 ) NOT NULL ,
//		 `attr_hidden` TINYINT( 1 ) NOT NULL ,
//		 `attr_system` TINYINT( 1 ) NOT NULL ,
//		 `attr_volume` TINYINT( 1 ) NOT NULL ,
//		 `attr_directory` TINYINT( 1 ) NOT NULL ,
//		 `attr_archive` TINYINT( 1 ) NOT NULL ,
//		 `type` MEDIUMINT( 7 ) UNSIGNED NOT NULL ,
//		 `index_date` DATE NOT NULL ,
//		PRIMARY KEY (  `id` ) ,
//		UNIQUE KEY  `url` (  `url` ) ,
//		KEY  `parent` (  `parent` ) ,
//		KEY  `size` (  `size` ) ,
//		KEY  `last_modified` (  `last_modified` ) ,
//		KEY  `index_date` (  `index_date` )
//		) ENGINE = MEMORY DEFAULT CHARSET = latin1;
///////////////////////////////////////////////////////////////////////////////	
////END TABLE SCHEMA
///////////////////////////////////////////////////////////////////////////////