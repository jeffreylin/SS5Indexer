package epiphany.ss5.objects;

import jcifs.netbios.NbtAddress;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/** 
 * Class that contains server information from the indexer 
 * @author Jeffrey Lin
 */
public class SS5Server {	
	
	//
	//Variables
	//
	
	String netbiosName;
	String ip;
	String mac;
	long indexDate;
	
	
	//
	//Public Constructor
	//
	/** Object that retrieves server information */
	public SS5Server(String netbiosName){
		this.netbiosName = netbiosName.toLowerCase();
		setIP(netbiosName);
		setMAC(ip);
		setIndexDate();
	}
	
	/** Object that retrieves server information */
	public SS5Server(String ip, String mac){
		setNetbiosName(ip);
		this.ip = ip;
		if(!mac.equals("000000000000")){this.mac = mac.toLowerCase();}
		else{setMAC(ip);};
		setIndexDate();
	}
	
	/** 
	 * Updates Netbios name of this server entry using Netbios name lookup
	 * @return	Returns the netbios name
	 */
	public String updateNetbiosName(){
		setNetbiosName(ip);
		return netbiosName;
	}
	
	//
	//Protected Methods
	//
	/** Get the Netbios name by lookup and set the netbios name for this entry */
	protected void setNetbiosName(String ip){
		try {
			NbtAddress[] allNetbiosNames = NbtAddress.getAllByAddress(ip);
			if(allNetbiosNames.length > 0){netbiosName = allNetbiosNames[0].getHostName().toLowerCase();}
		} catch (UnknownHostException e) {}//e.printStackTrace();}
	}
	
	/** Get the IP name by lookup and set the IP for this entry */
	protected void setIP(String netbiosName){
		try {
			jcifs.UniAddress address = jcifs.UniAddress.getByName(netbiosName);
			ip = correctForLocalhostIP(address.getHostAddress());
		} catch (UnknownHostException e) {}//e.printStackTrace();}
	}
	
	/** 
	 * Get the MAC address (if able) and set the MAC address for this entry. 
	 * Sets a MAC address of 000000000000 if unable to determine MAC address
	 */
	protected void setMAC(String netbiosNameOrIP){
		try {
			mac = getHex(jcifs.netbios.NbtAddress.getByName(ip).getMacAddress());
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			mac = "000000000000";
		}
	}
	
	/** Sets the index date of this server */ 
	protected void setIndexDate(){
		indexDate = (new java.util.Date()).getTime();
	}

	
	//
	//Private Methods
	//
	
	private String POMONA_SUBNET = "134";	//first octet of network IP
											//ex: 134.173.47.47 -> "134"
											//this is for finding our own ip
	private String LOCALHOST_SUBNET = "127";//first octet of localhost IP
											//ex: 127.0.0.1 -> "127
											//this is identifying the localhost
											//IP so we can change it to the
											//correct one
											//Change this setting if using IPv6

	/** 
	 * Convenience method to filter out the localhost IP and replace it with
	 * the server's external IP
	 */
	private String correctForLocalhostIP(String input) {
		if (input.substring(0, LOCALHOST_SUBNET.length()).equals(LOCALHOST_SUBNET)) {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();
				Enumeration<InetAddress> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ip = e2.nextElement();
					if (ip.toString().substring(0, POMONA_SUBNET.length() + 1).equals(
							"/" + POMONA_SUBNET)) {
						return ip.toString().substring(1);
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		}
		return input;
	}

	/** 
	 * Copied from: http://www.rgagnon.com/javadetails/java-0596.html 
	 * @param raw	byte representation of a string
	 * @return 	String representation given an array of bytes 
	 */
	private static String getHex(byte[] raw) {
		final String HEXES = "0123456789abcdef";
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	
	//
	//Getter Methods
	//
	
	public String getNetbiosName() {return netbiosName;}
	public String getIP() {return ip;}
	public String getMAC() {return mac;}
	public long getIndexDate() {return indexDate;}
	
	public String toString(){return netbiosName+";"+ip+";"+mac+";"+indexDate;}
}
