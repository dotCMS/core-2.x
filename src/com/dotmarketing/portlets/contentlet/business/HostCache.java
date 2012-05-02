package com.dotmarketing.portlets.contentlet.business;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.Cachable;

//This interface should have default package access
public abstract class HostCache implements Cachable{
	protected static String PRIMARY_GROUP = "HostCache";
	protected static String ALIAS_GROUP = "HostAliasGroup";
	
	abstract protected Host add(Host host);

	abstract protected Host get(String key);

	abstract public void clearCache();

	abstract protected void remove(Host host);

	abstract protected Host getDefaultHost();
	
	abstract protected Host getHostByAlias(String alias);
	
}