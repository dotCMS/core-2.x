package com.dotmarketing.portlets.contentlet.business;

import com.dotmarketing.business.Cachable;

//This interface should have default package access
public abstract class ContentletCache implements Cachable{

	abstract protected com.dotmarketing.portlets.contentlet.model.Contentlet add(String key,com.dotmarketing.portlets.contentlet.model.Contentlet content);

	abstract protected com.dotmarketing.portlets.contentlet.model.Contentlet get(String key);

	public abstract void clearCache();

	abstract protected void remove(String key);

}