package com.dotmarketing.business;

/**
 * 
 * @author Jason Tesser
 *
 */
public abstract class LayoutCache implements Cachable {

	abstract protected Layout add(String key,Layout layout);

	abstract protected Layout get(String key);
	
	abstract public void clearCache();

	abstract protected void remove(String key);

}
