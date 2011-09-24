package epiphany.ss5.main;

import java.util.Vector;
import epiphany.ss5.objects.SS5Server;

/**
 * Class to generate a list of Samba servers to index
 * @author Jeffrey Lin
 *
 */
public abstract class ServerListGenerator {
	
	//
	//protected methods
	//
	
	/**
	 * Method you should call to get servers to index if you already know the
	 * names of the servers you want to index.
	 * @param serversToIndex	Names of servers to index
	 * @return Server objects to index
	 */
	protected Vector<SS5Server> useServerList(String[] serversToIndex){
		return getValidServersFromNameList(serversToIndex);
	}
	
	/**
	 * Method you should call to scan for servers and index those servers
	 * @return Server objects to index
	 */
	protected Vector<SS5Server> useNmap(){
		Vector<SS5Server> vs = (new NmapXMLParser()).getServers();
		Vector<SS5Server> output = new Vector<SS5Server>();
		for(SS5Server s : vs){
			if(isValidServerName(s.getNetbiosName())){
				output.add(s);
			}
		}
		return output;
	}
	
	/**
	 * Method you should call to use a previous XML Nmap log as a list of
	 * possible servers to index
	 * @param xmlFilepath	Filepath to the XML Nmap log
	 * @return Server objects to index
	 */
	protected Vector<SS5Server> useNmap(String xmlFilepath){
		Vector<SS5Server> vs = (new NmapXMLParser(xmlFilepath)).getServers();
		Vector<SS5Server> output = new Vector<SS5Server>();
		for(SS5Server s : vs){
			if(isValidServerName(s.getNetbiosName())){
				output.add(s);
			}
		}
		return output;
	}

	
	//
	//private methods
	//
	private Vector<SS5Server> getValidServersFromNameList(String[] serverNames){
		Vector<SS5Server> listOfServers = new Vector<SS5Server>();
		for(String serverName : serverNames){
			SS5Server s = new SS5Server(serverName);
			if(isValidServerName(s.getNetbiosName()) && s.getIP() != null){
				listOfServers.add(s);
			}
		}
		return listOfServers;
	}
	
	/**
	 * @param name	Server name
	 * @return True if the server name is valid, false otherwise.
	 */
	protected boolean isValidServerName(String name){
		if(name == null || name.equals("0.0.0.0") || name.equals("")){
			return false;
		}
		else{
			return true;
		}
	}

	
	/**@deprecated*/
	protected Vector<SS5Server> getOldServerList(){
		String[] serversToIndex = { "server_name_1", "server_name_2", "Etc..." };
		return useServerList(serversToIndex);
	}
}
