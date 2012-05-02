package com.dotmarketing.portlets.files.business;

import com.dotmarketing.business.Cachable;
import com.dotmarketing.portlets.files.model.File;

//This interface should have default package access
public abstract class FileCache implements Cachable {

	abstract protected File add(String key, File file);

	abstract protected File get(String key);

	public abstract void clearCache();

	abstract protected void remove(String key);
}