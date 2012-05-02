package com.dotmarketing.portlets.files.business;

import java.io.FilenameFilter;
import java.util.List;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.WebKeys;

public class ThumbnailsFileNamesFilter implements FilenameFilter {
	List<File> versions;
	
	@SuppressWarnings("unchecked")
	public ThumbnailsFileNamesFilter (Identifier fileIden) {
		versions = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(fileIden, File.class);
	}
	
	public boolean accept(java.io.File dir, String name) {
		for (File file : versions) {
			if (name.startsWith(String.valueOf(file.getInode()) + "_thumb") 
					|| name.startsWith(String.valueOf(file.getInode()) + "_resized")
					|| name.contains(WebKeys.GENERATED_FILE)
			)
				return true;
		}
		return false;
	}
}