package com.dotmarketing.portlets.htmlpages.business;

import com.dotmarketing.business.Cachable;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;

//This interface should have default package access
public abstract class HTMLPageCache implements Cachable {

	abstract protected HTMLPage add(HTMLPage htmlPage);

	abstract protected HTMLPage get(String key);

	abstract public void clearCache();

	abstract public void remove(HTMLPage page);
}