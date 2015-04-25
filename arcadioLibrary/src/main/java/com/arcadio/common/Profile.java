package com.arcadio.common;



public class Profile {
	/**
	 * @author Alberto Azuara García 02/12/2014
	 *
	 */

	private String name;
	private String ip;
	private int port;
	private String password;


	public Profile(String name, String ip, int port, String password) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.password = password;
	}
	public Profile(String _nombre){
		this.name = _nombre;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
