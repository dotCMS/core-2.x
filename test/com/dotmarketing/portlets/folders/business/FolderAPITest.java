package com.dotmarketing.portlets.folders.business;

import java.util.List;

import org.apache.cactus.ServletTestCase;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.model.Folder;

public class FolderAPITest extends ServletTestCase {

	private FolderAPI folderAPI;
	
	public void setUp () {
		folderAPI = APILocator.getFolderAPI();
	}

	public void testFindSubFolders() {
		
		Host defaultHost;
		try {
			defaultHost = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotDataException e) {
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			throw new DotRuntimeException(e.getMessage(), e);
		}

		List<Folder> folders = folderAPI.findSubFolders(defaultHost);
		assertEquals(8, folders.size());
		assertTrue(containsFolderByName(folders, "application"));
		assertTrue(containsFolderByName(folders, "blog"));
		assertTrue(containsFolderByName(folders, "calendar"));
		assertTrue(containsFolderByName(folders, "getting_started"));
		assertTrue(containsFolderByName(folders, "global"));
		assertTrue(containsFolderByName(folders, "home"));
		assertTrue(containsFolderByName(folders, "news"));
		assertTrue(containsFolderByName(folders, "store"));
		
	}
	
	public void testFindSubFoldersRecursivelyByFolder() {
		Folder f = folderAPI.findFolderByPath("/getting_started");
		List<Folder> folders = folderAPI.findSubFoldersRecursively(f);
		assertEquals(4, folders.size());
		assertTrue(containsFolderByName(folders, "macros"));
		assertTrue(containsFolderByName(folders, "samples"));
		assertTrue(containsFolderByName(folders, "professional_support"));
		assertTrue(containsFolderByName(folders, "widgets"));
		
	}
	
	public void testFindSubFoldersRecursivelyByHost() {
		
		Host defaultHost;
		try {
			defaultHost = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotDataException e) {
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			throw new DotRuntimeException(e.getMessage(), e);
		}

		List<Folder> folders = folderAPI.findSubFoldersRecursively(defaultHost);
		assertEquals(40, folders.size());
		assertTrue(containsFolderByName(folders, "macros"));
		assertTrue(containsFolderByName(folders, "swf"));
		assertTrue(containsFolderByName(folders, "products"));
		assertTrue(containsFolderByName(folders, "product-images"));
		assertTrue(containsFolderByName(folders, "samples"));
		assertTrue(containsFolderByName(folders, "professional_support"));
		assertTrue(containsFolderByName(folders, "blog"));
		assertTrue(containsFolderByName(folders, "widgets"));
	}

	private boolean containsFolderByName(List<Folder> folders, String name) {
		for(Folder f: folders) {
			if(f.getName().trim().equals(name.trim()))
				return true;
		}
		return false;
	}

}
