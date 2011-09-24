package epiphany.ss5.main;

import java.util.LinkedList;
import java.util.List;

import epiphany.ss5.db.MySQL_ServerStorer;
import epiphany.ss5.db.SS5FileStorer;
import epiphany.ss5.db.MySQL_FileStorer;
import epiphany.ss5.db.SS5ServerStorer;
import epiphany.ss5.objects.SS5File;
import epiphany.ss5.objects.SS5Server;

/** 
 * Class that indexes Samba servers
 * TODO: lots!
 * TODO: ServerListGenerator should probably be encapsulated rather than 
 * extended...
 * @author Jeffrey Lin
 */
public class SambaIndexer extends ServerListGenerator {
	
	//
	//Constants
	//
	/** Maximum execution time for each thread */
	int THREAD_TIME_LIMIT = 1800000;	//in milliseconds
	/** Maximum number of concurrent indexing threads */
	int MAX_NUM_THREADS = 5;
	
	//
	//Variables
	//
	/** Server entry storage engine */
	SS5ServerStorer serverStorer = new MySQL_ServerStorer();
	
	//
	//Public methods
	//
	/**
	 * Indexes a list of possible Samba servers using multiple threads. 
	 * Luckily, MySQL takes care of most concurrency issues for us. 
	 * Because only one master thread commands the worker threads we ought to
	 * be okay concurrency-wise.
	 * 
	 * TODO: better dead thread pruning
	 * 
	 * @param serverList	List of servers to index
	 */
	public void indexSambaShares(List<SS5Server> serverList){
		System.setProperty("jcifs.netbios.cachePolicy", "-1");		//i think this helps, not sure
		System.setProperty("jcifs.netbios.retryTimeout", "300");

		System.out.println("Will index:");
		for(SS5Server s : serverList){
			System.out.println(s);
		}

		int secondsElapsed = 0;
		LinkedList<IndexerThread> listOfThreads = new LinkedList<IndexerThread>(); 
		while(!serverList.isEmpty() || !listOfThreads.isEmpty()){
			System.out.println(
				secondsElapsed+" seconds have elapsed " + "(" 
				+serverList.size()+ " servers left, " +
				+listOfThreads.size()+" threads running)."
			);
			
			//stop finished threads
			for(IndexerThread it : listOfThreads){
				if(it.timeElapsed() > THREAD_TIME_LIMIT){
					it.stopThread();
				}
			}
			
			//prune dead threads	=>	there's got to be a better way to do this...
			for(int i = 0; i<listOfThreads.size(); i++){
				IndexerThread it = listOfThreads.pop();
				if(it.isAlive()){
					listOfThreads.add(it);
				}
			}

			//add threads if necessary
			while(!serverList.isEmpty() && listOfThreads.size() < MAX_NUM_THREADS){
				listOfThreads.add(indexServer(serverList.remove(0)));
			}
			
			try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
			secondsElapsed=secondsElapsed+10;
		}

		System.out.println("Finished indexing.");
		serverStorer.close();
	}
	
	
	//
	//Protected (perhaps should be private) methods
	//
	/**
	 * Spawns a thread to index the files of a server
	 * @param s	Server entry to index
	 */
	protected IndexerThread indexServer(SS5Server s){
		//in case the server IP has changed
		s.updateNetbiosName();
		serverStorer.storeServer(s);
		
		//start indexing thread
		IndexerThread it = new IndexerThread(new SS5File(s,0));	//new IndexerThread(server);
		it.setServerName(s.getNetbiosName());
		it.start();
		return it;
	}
	
	
	//
	//IndexerThread Class
	//
	/**
	 * Seperate thread to index the files of a server
	 */
	protected class IndexerThread extends java.lang.Thread{
		SS5File rootFileToIndex;
		SS5FileStorer fileStorer = new MySQL_FileStorer();
		String serverName;
		boolean running = true;
		long startTime = System.currentTimeMillis();
		
		//
		//Constructor
		//
		public IndexerThread(SS5File rootFileToIndex){
			this.rootFileToIndex = rootFileToIndex;
		}
		
		//
		//java.lang.Thread required method
		//
		/**
		 * Required java.lang.Thread method; 
		 * Recursively indexes starting from the root file associated with this 
		 * thread.
		 */
		public void run(){
			System.out.println("Starting to index: "+serverName);
			indexFile(rootFileToIndex);	//indexStack(filesToIndex);
			System.out.println("Took "+(System.currentTimeMillis()-startTime)/1000+" seconds to index: "+serverName);
		}
		
		//
		//Protected methods
		//
		/** Recursively indexes the current file using a depth first search. 
		 * TODO: identify infinite link loops? hasn't been an issue so far 
		 * TODO: better way to terminate thread when the max thread execution
		 *       time is exceeded
		 */
		protected void indexFile(SS5File currentFile){
			try{
				if(!running){return;}	// poll running state
				currentFile.setProperties();
				currentFile.storeEntry(fileStorer);
				
				if(currentFile.getATTR_Directory()){	//it's a directory
					SS5File[] children = currentFile.getChildren();
					for(SS5File f : children){
						indexFile(f);
						currentFile.incrementSize(f.getSize());	//add filesize to folder
					}
					children = null;	//remove reference so it can be garbage collected
					currentFile.storeEntry(fileStorer);	//update the size
				}
			}catch(java.lang.OutOfMemoryError e){
				System.out.println("Error indexing: "+currentFile);
				e.printStackTrace();
				running = false;
			}
		}
		
		/** Sets the server for a file entry */
		public void setServerName(String s){
			serverName = s;
		}
		
		/** Sets the time elapsed for the current thread */
		protected long timeElapsed(){	//in milliseconds
			return System.currentTimeMillis() - startTime;
		}
		
		/** 
		 * Stops the thread if the running variable is false. 
		 * This allows the master thread to stop this thread if necessary.
		 */
		protected void stopThread(){
			running = false;
		}
		
	}
	
}
