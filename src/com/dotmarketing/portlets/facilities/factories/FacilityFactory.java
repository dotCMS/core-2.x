/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dotmarketing.portlets.facilities.factories;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.facilities.model.Facility;

/**
 * @author maria
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FacilityFactory {
	
	public static Facility getFacility(String inode) {
		return (Facility) InodeFactory.getInode(inode,Facility.class);
	}
	
	public static void deleteFacility(Facility f) {
		InodeFactory.deleteInode(f);
	}

}
