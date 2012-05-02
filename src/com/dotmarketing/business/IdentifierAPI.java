package com.dotmarketing.business;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.exception.DotDataException;

public interface IdentifierAPI {

	/**
	 * Used to return the identifier of a given inode id 
	 * @param inode
	 * @return
	 * @throws DotDataException
	 * @throws DotIdentifierStateException if no identifier can be found from passed in inode
	 */
	public Identifier findFromInode(String inode) throws DotDataException, DotIdentifierStateException;
	
	
	/**
	 * Used to return the identifier of a given string id 
	 * @param id
	 * @return
	 * @throws DotDataException
	 * @throws DotIdentifierStateException if no identifier can be found from passed in id
	 */
	public Identifier find(String id) throws DotDataException, DotIdentifierStateException;
	
	
	
	
	/**
	 * This method WILL HIT the DB. 
	 * @return
	 * @throws DotDataException
	 */
	public boolean isIdentifier(String identifierInode) throws DotDataException;
	
	public  java.util.List findVersionsandLiveandWorkingChildrenOfClass(Inode o, Class c);
}
