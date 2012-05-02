package com.dotmarketing.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {

	
    private static Set<String> extensions = new HashSet<String>();
    
    
    /**
     * This method takes a string of a filename or extension and maps it
     * to a known .png file in the /html/image/icons/directory
     * @param x is the filename or extension
     * @return
     */
    
	public static String getIconExtension(String x){
		
		if(x.indexOf(".") > -1){
			x = x.substring(x.indexOf("."), x.length());
		}
		
		
		if(extensions.size() ==0){
			synchronized (FileUtil.class) {
				if(extensions.size() == 0){
					String path = Config.CONTEXT.getRealPath("/html/images/icons");

					String[] files = new File(path).list(new PNGFileNameFilter());
					for(String name : files){
						if(name.indexOf(".png") > -1)
							extensions.add(name.replace(".png", ""));
					}
				}
			}
		}
		// if known extension
		if(extensions.contains(x)){
			return x;
		}
		else{
			return "ukn";
		}
		
		
	}
	
}
class PNGFileNameFilter implements FilenameFilter{
	public boolean accept(File dir, String name) {
		return (name.indexOf(".png") > -1);
	}
	
}
