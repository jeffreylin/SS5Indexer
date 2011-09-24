package epiphany.ss5.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Class for calculating and storing folder content sizes for folders
 * @author Jeffrey Lin
 *
 */
public abstract class MySQL_FolderSizer extends MySQL_Abstract {
	
	//
	//Constants
	//
	
	/** ID for the root "folder" */
	protected static final int INIT_FOLDER_ID = 0;
	
	/** MySQL template query to get the child folders of a target folder */
	protected static final String GET_CHILD_FOLDERS_PREPARED_STATEMENT =
		"SELECT `id` FROM `"+FILES_TABLE+"` " +
		"WHERE `parent` = ? AND " +
		"`attr_directory` = 1"
		;
	
	/** MySQL template query to get the size of all the children of a folder */
	protected static final String GET_FOLDER_SIZE_PREPARED_STATEMENT = 
		"SELECT SUM( `size` ) FROM `"+FILES_TABLE+"` WHERE `parent` = ?"
		;
	
	/** MySQL template query to update the folder size of a folder */
	protected static final String UPDATE_FOLDER_SIZE_PREPARED_STATEMENT = 
		"UPDATE `"+FILES_TABLE+"` " +
		"SET `size` = ? " +
		"WHERE `id` = ? " +
		"LIMIT 1;"
		;
	
	
	//
	//Variables
	//
	
	private java.sql.PreparedStatement getChildFoldersPS;
	private java.sql.PreparedStatement getFolderSizePS;
	private java.sql.PreparedStatement updateFolderSizePS;
	
	
	//
	//Public Methods
	//
	
    /** Calculates and stores folder content sizes for folders */
	public MySQL_FolderSizer(){
		try {		
			getChildFoldersPS = con.prepareStatement(GET_CHILD_FOLDERS_PREPARED_STATEMENT);
			getFolderSizePS = con.prepareStatement(GET_FOLDER_SIZE_PREPARED_STATEMENT);
			updateFolderSizePS = con.prepareStatement(UPDATE_FOLDER_SIZE_PREPARED_STATEMENT);
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	/** Recursively update folder sizes starting with the root folder */
	public void updateFolderSizes(){
		updateFolderSize(INIT_FOLDER_ID);
	}
	
	
	//
	//Private Methods
	//
	
	/**
	 * Method to update the folder size of a given folder ID
	 * 
	 * How this works:
	 * [for loop]
	 * 	recursively updates folder sizes for all children
	 * [if... then... setFolderSize()]
	 * 	Now that all the children folder sizes are updated, 
	 *  we can update this folder's file size
	 *  Also, we check to make sure we don't update the
	 *  INIT_FOLDER_ID, because it doesn't actually have a row
	 *  associated with it
	 *  
	 *  Might possibly make this public in the future so we can update folder 
	 *  sizes from arbitrary folders
	 *  ^The problem in this case is that parent folders don't get updated
	 *  
	 * @param id
	 */
	private void updateFolderSize(int id){
		for(int fid : getChildFolders(id)){updateFolderSize(fid);}
		if(id != INIT_FOLDER_ID){setFolderSize(getFolderSize(id), id);}
	}
	
	/**
	 * Method to get children folders
	 * @param id	ID of the parent folder
	 * @return	Children folders
	 */
	private Iterable<Integer> getChildFolders(int id){
		try {
			
			//execute MySQL statement to get child folders
			getChildFoldersPS.setInt(1, id);
			ResultSet results = getChildFoldersPS.executeQuery();
			
			//put all of our results in a linked list that we return
			LinkedList<Integer> output = new LinkedList<Integer>();
			while(results.next()){output.add(results.getInt(1));}
			return output;	//sweet, everything worked.
			
		} catch (SQLException e) {e.printStackTrace();}
		return null;	//something went wrong if we reach this line
	}

	/**
	 * Method to get the size of a folder
	 * @param id Parent folder ID
	 * @return Folder content size (includes children directory totals as well
	 *         as files)
	 */
	private long getFolderSize(int id){
		try {
			getFolderSizePS.setInt(1, id);
			ResultSet rs = getFolderSizePS.executeQuery();
			rs.first();
			return rs.getLong(1);
		} catch (SQLException e) {e.printStackTrace();}
		return -1L;	//something went wrong
	}
	
	/**
	 * Method to set the folder size of a target folder entry
	 * @param size	Folder size
	 * @param id	Target folder ID
	 */
	private void setFolderSize(long size, int id){
		try {
			updateFolderSizePS.setLong(1, size);
			updateFolderSizePS.setInt(2, id);
			updateFolderSizePS.execute();
		} catch (SQLException e) {e.printStackTrace();}
	}

}
