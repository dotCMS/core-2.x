package com.dotmarketing.portlets.contentlet.business;

import java.io.FileFilter;

import com.dotmarketing.util.Config;
import com.dotmarketing.util.WebKeys;

public class BinaryFileFilter implements FileFilter {


	public boolean accept(java.io.File pathname) {
		if(pathname.getName().contains(WebKeys.GENERATED_FILE)){
			return false;
		}
		if(pathname.getName().startsWith(".")){
			return false;
		}
		else{
			return true;
		}
	}


}
