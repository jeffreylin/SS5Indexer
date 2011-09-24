package epiphany.ss5.main;

import java.util.Vector;
import epiphany.ss5.db.SS5TableManager;
import epiphany.ss5.db.MySQL_TableManager;
import epiphany.ss5.objects.SS5Server;

/**
 * Main class.
 * Indexes Samba shares in the Pomona ResNet.
 * @author Jeffrey Lin
 *
 */
public class SS5Indexer extends SambaIndexer{
	
	/**
	 * Basically a main method that is run.
	 */
	public SS5Indexer(){
		System.out.println("Running SS5Indexer Beta 9");
		System.out.println("Max memory: "+Runtime.getRuntime().maxMemory());
		Vector<SS5Server> serverList = useNmap();
		indexSambaShares(serverList);
		
		SS5TableManager tm = new MySQL_TableManager();
		System.out.println("Removing old server entries...");
		tm.removeOldServerEntries();
		System.out.println("Removing old file entries...");
		tm.removeOldFileEntries();
//		System.out.println("Updating folder sizes...");	//this is taken care of already in SambaIndexer
//		tm.updateFolderSizes();
		System.out.println("Optimizing tables...");
		tm.optimizeTables();
		tm.close();
		System.out.println("Done.");
	}
	
	public static final void main(String[] args){
		new SS5Indexer();
	}
	
}
