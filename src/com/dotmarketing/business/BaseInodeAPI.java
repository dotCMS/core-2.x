/**
 * 
 */
package com.dotmarketing.business;

import java.util.List;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.factories.InodeFactory;



/**
 * @author jtesser
 * All methods in the BaseInodeAPI should be protected or private. The BaseInodeAPI is intended to be extended by other APIs for Inode Objects. 
 */
public abstract class BaseInodeAPI {
	
	/**
	 * Will return all children for the passed in condition 
	 * @param inodeParent
	 * @param inodeClassToReturn
	 * @param condition
	 * @return
	 */
	protected List getChildrenClassByCondition(Inode inodeParent, Class inodeClassToReturn, String condition) {
		return InodeFactory.getChildrenClassByCondition(inodeParent, inodeClassToReturn, condition, 0, 0);
	}

	protected void saveInode(Inode inode) throws DotDataException {
		HibernateUtil.save(inode);
	}
}
