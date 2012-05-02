package com.dotmarketing.beans;

import java.io.Serializable;

/**
 * 
 * @author maria
 */
public class Identifier extends Inode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1895228885287457403L;

	/**
	 * 
	 */


	public Identifier() {
		this.setType("identifier");
	}

	private String URI;

	private String hostId;

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	/**
	 * Returns the uRI.
	 * 
	 * @return String
	 */
	public String getURI() {
		return URI;
	}

	/**
	 * Sets the uRI.
	 * 
	 * @param uRI
	 *            The uRI to set
	 */
	public void setURI(String uRI) {
		URI = uRI;
	}

}
