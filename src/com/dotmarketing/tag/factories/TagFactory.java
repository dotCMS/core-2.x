package com.dotmarketing.tag.factories;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.tag.model.Tag;
import com.dotmarketing.tag.model.TagInode;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;

public class TagFactory {

	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();

	/**
	 * @param permissionAPI the permissionAPI to set
	 */
	public static void setPermissionAPI(PermissionAPI permissionAPIRef) {
		permissionAPI = permissionAPIRef;
	}

	/**
	 * Get a list of all the tags created
	 * @return list of all tags created
	 */
	public static java.util.List<Tag> getAllTags() {
		try {
			DotHibernate dh = new DotHibernate(Tag.class);
			dh.setQuery("from tag in class com.dotmarketing.tag.model.Tag");
            List list = dh.list();
        	return list;
		}
		catch (Exception e) {}
		return new ArrayList();
	}
	
	/**
	 * Get a list of all the tags name created
	 * @return list of all tags name created
	 */
	public static java.util.List<String> getAllTagsName() {
		try {
			List<String> result = new ArrayList<String>();
			
			List<Tag> tags = getAllTags();
			for (Tag tag: tags) {
				result.add(tag.getTagName());
			}
			
			return result;
		}
		catch (Exception e) {}
		return new ArrayList();
	}

	/**
	 * Gets a Tag by name
	 * @param name name of the tag to get
	 * @return tag
	 */
	public static java.util.List<Tag> getTagByName(String name) {
        try {
			name = escapeSingleQuote(name);

			DotHibernate dh = new DotHibernate(Tag.class);
            dh.setQuery("from tag in class com.dotmarketing.tag.model.Tag where lower(tagName) = ?");
            dh.setParam(name.toLowerCase());

            List list = dh.list();
        	return list;
        } catch (Exception e) {
            Logger.warn(Tag.class, "getTagByName failed:" + e, e);
        }
        return new ArrayList();
    }

	/**
	 * Gets all the tag created by an user
	 * @param userId id of the user
	 * @return a list of all the tags created
	 */
	public static java.util.List<Tag> getTagByUser(String userId) {
        try {
            DotHibernate dh = new DotHibernate(Tag.class);
            dh.setQuery("from tag in class com.dotmarketing.tag.model.Tag where user_id = ?");
            dh.setParam(userId);

            List list = dh.list();
            
        	return list;
        } catch (Exception e) {
            Logger.warn(Tag.class, "getTagByUser failed:" + e, e);
        }
        return new java.util.ArrayList();
	}

	/**
	 * Gets a Tag by name, validates the existance of the tag, if it doesn't exists then is created
	 * @param name name of the tag to get
	 * @param userId owner of the tag
	 * @return tag
	 */
	public static Tag getTag(String name, String userId) {
		
		// validating if exists a tag with the name provided
        DotHibernate dh = new DotHibernate(Tag.class);
        dh.setQuery("from tag in class com.dotmarketing.tag.model.Tag where lower(tagName) = ?");
        dh.setParam(name.toLowerCase());

        Tag tag = (Tag) dh.load();
        // if doesn't exists then the tag is created
        if (tag.getTagName() == null) {
        	// creating tag
        	return addTag(name, userId);
        }
        // returning tag
        return tag;
	}

	/**
	 * Creates a new tag
	 * @param tagName name of the new tag
	 * @param userId owner of the new tag
	 * @return new tag created
	 */
    public static Tag addTag(String tagName, String userId) {
		//creates new Tag
    	Tag tag = new Tag();
    	tag.setTagName(tagName);
    	tag.setUserId(userId);
        DotHibernate.save(tag);
        return tag;
    }
    
	/**
	 * Tags an object, validates the existence of a tag(s), creates it if it doesn't exists
	 * and then tags the object 
	 * @param tagName tag(s) to create
	 * @param userId owner of the tag
	 * @param inode object to tag
	 * @return a list of all tags assigned to an object
	 */
	public static List addTag(String tagName, String userId, String inode) {
		StringTokenizer tagNameToken = new StringTokenizer(tagName, ",");
		if (tagNameToken.hasMoreTokens()) {
	    	for (; tagNameToken.hasMoreTokens();) {
	    		String tagTokenized = tagNameToken.nextToken().trim();
	    		TagFactory.getTag(tagTokenized, userId);
	    		TagFactory.addTagInode(tagTokenized, inode);
	    	}
		}
		return getTagInodeByInode(inode);
	}

	/**
     * Deletes a tag
     * @param tag tag to be deleted
     */
    public static void deleteTag(Tag tag) {
        DotHibernate.delete(tag);
    }

    /**
     * Deletes a tag
     * @param tagName name of the tag to be deleted
     * @param userId id of the tag owner
     */
	public static void deleteTag(String tagName, String userId) {
		Tag tag = getTag(tagName, userId);
    	DotHibernate.delete(tag);
	}

	/**
	 * Renames a tag
	 * @param tagName new tag name
	 * @param oldTagName current tag name 
	 * @param userId owner of the tag
	 */
	public static void editTag(String tagName,String oldTagName, String userId) {
		try {
			tagName = escapeSingleQuote(tagName);
			oldTagName = escapeSingleQuote(oldTagName);

			List tagToEdit = getTagByName(oldTagName);
			Iterator it = tagToEdit.iterator();
			for (int i = 0; it.hasNext(); i++) {
				Tag tag = (Tag)it.next();
				
				tag.setTagName(tagName);
				DotHibernate.saveOrUpdate(tag);
			}
		}
		catch (Exception e) {}
	}

	/**
	 * Gets all the tags created, with the respective owner and permission information
	 * @param userId id of the user that searches the tag
	 * @return a complete list of all the tags, with the owner information and the respective permission
	 * information
	 */
	public static List getAllTag(String userId) {
		try {
			User searcherUser = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);

			DotHibernate dh = new DotHibernate();
			StringBuffer sb = new StringBuffer();
			sb.append("select Tag.*, User_.firstName, User_.lastName from Tag, User_ ");
			sb.append("where Tag.user_id = User_.userid ");
			sb.append("order by Tag.user_id");
			dh.setQuery(sb.toString());
	
			List allTags = dh.list();
		
			java.util.List matchesArray = new ArrayList();
			Iterator it = allTags.iterator();
			for (int i = 0; it.hasNext(); i++) {
				User user = null;

				Map map = (Map)it.next();

				String user_Id = (String) map.get("user_id");
				String tagName = (String) map.get("tagname");
				String firstName = (String) map.get("firstname");
				String lastName = (String) map.get("lastname");
				user = APILocator.getUserAPI().loadUserById(user_Id,APILocator.getUserAPI().getSystemUser(),false);
				UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);

				String[] match = new String[6]; 
				match[0] = (user_Id==null)?"":user_Id;
				match[1] = (tagName==null)?"":tagName;
				match[2] = (firstName==null)?"":firstName;
				match[3] = (lastName==null)?"":lastName;

				// adding read permission
				try {
					_checkUserPermissions(userProxy, searcherUser, PERMISSION_READ);
					match[4] = "true";
				} catch (ActionException ae) {
					match[4] = "false";
				}

				// adding write permission
				try {
					_checkUserPermissions(userProxy, searcherUser, PERMISSION_WRITE);
					match[5] = "true";
				} catch (ActionException ae) {
					match[5] = "false";
				}
				matchesArray.add(match);
			}
				
			return matchesArray;
		}
		catch (Exception e) {}

		return new ArrayList();
	}

	/**
	 * Gets a tag with the owner information, searching by name
	 * @param name name of the tag
	 * @return the tag with the owner information 
	 */
	public static List getTagInfoByName(String name) {
		try {
			name = escapeSingleQuote(name);

			DotHibernate dh = new DotHibernate();
			StringBuffer sb = new StringBuffer();
			sb.append("select Tag.*, User_.firstName, User_.lastName from Tag, User_ ");
			sb.append("where Tag.user_id = User_.userid and ");
			sb.append("lower(Tag.tagName) like '%"+name.toLowerCase()+"%' ");
			sb.append("order by Tag.user_id");
	
			dh.setQuery(sb.toString());
			
			java.util.List allTags = dh.list();

			return allTags;
		}
		catch (Exception e) {}

		return new ArrayList();
	}

	/**
	 * Checks the permission access of an user over an object 
	 * @param webAsset object to validates access
	 * @param user user to validate access
	 * @param permission read or write permission to validates
	 * @throws ActionException
	 * @throws DotDataException 
	 */
	protected static void _checkUserPermissions(Inode webAsset, User user,
			int permission) throws ActionException, DotDataException {
		// Checking permissions
		if (!InodeUtils.isSet(webAsset.getInode()))
			return;

		if (!permissionAPI.doesUserHavePermission(webAsset, permission,
				user)) {
			throw new ActionException(WebKeys.USER_PERMISSIONS_EXCEPTION);
		}
	}

	/**
	 * Gets a tagInode, if doesn't exists then the tagInode it's created
	 * @param tagName name of the tag
	 * @param inode inode of the object tagged
	 * @return a tagInode
	 */
    public static TagInode addTagInode(String tagName, String inode) {
    	
    	//Ensure the tag exists in the tag table
    	Tag existingTag = getTag(tagName, "");

    	//validates the tagInode already exists
		TagInode existingTagInode = getTagInode(tagName, inode);
    	if (existingTagInode.getTagId() == null) {
	
	    	//the tagInode does not exists, so creates a new TagInode
	    	TagInode tagInode = new TagInode();
	    	tagInode.setTagId(existingTag.getTagName());
	    	/*long i = 0;
	    	try{
	    		i =Long.parseLong(inode);
	    	}catch (Exception e) {
				Logger.error(TagFactory.class, "Unable to get Long value from " + inode, e);
			}*/
	    	tagInode.setInode(inode);
	        DotHibernate.saveOrUpdate(tagInode);

	        return tagInode;
    	}
    	else {
    		// returning the existing tagInode
    		return existingTagInode;
    	}
    }

    /**
     * Gets all tags associated to an object
     * @param inode inode of the object tagged
     * @return list of all the TagInode where the tags are associated to the object
     */
	public static List getTagInodeByInode(String inode) {
        try {
            DotHibernate dh = new DotHibernate(Tag.class);
            dh.setQuery("from tag_inode in class com.dotmarketing.tag.model.TagInode where inode = ?");
            dh.setParam(inode);

            List list = dh.list();
        	return list;

        } catch (Exception e) {
            Logger.warn(Tag.class, "getTagInodeByInode failed:" + e, e);
        }
        return new ArrayList();
	}

	/**
	 * Gets a tagInode by name and inode
	 * @param name name of the tag
	 * @param inode inode of the object tagged
	 * @return the tagInode
	 */
	public static TagInode getTagInode(String name, String inode) {
		// getting the tag inode record
        DotHibernate dh = new DotHibernate(Tag.class);
        dh.setQuery("from tag_inode in class com.dotmarketing.tag.model.TagInode where lower(tagName) = ? and inode = ?");
        dh.setParam(name.toLowerCase());
        dh.setParam(inode);
        
        TagInode tagInode;
        try {
        	tagInode = (TagInode) dh.load();
        }
        catch (Exception ex) {
        	tagInode = new TagInode();
        }
        return tagInode;
	}

	/**
	 * Deletes a TagInode
	 * @param tagInode TagInode to delete
	 */
	public static void deleteTagInode(TagInode tagInode) {
        DotHibernate.delete(tagInode);
    }

	/**
	 * Deletes an object tag assignment(s)
	 * @param tagName name(s) of the tag(s)
	 * @param inode inode of the object tagged
	 * @return a list of all tags assigned to an object
	 * @throws Exception 
	 */
	
	public List deleteTagInode(String tagName, String inode) throws Exception {
		StringTokenizer tagNameToken = new StringTokenizer(tagName, ",");
		if (tagNameToken.hasMoreTokens()) {
	    	for (; tagNameToken.hasMoreTokens();) {
	    		String tagTokenized = tagNameToken.nextToken().trim();
	    		Tag tag = APILocator.getTagAPI().getTag(tagTokenized,"","");
	    		TagInode tagInode = getTagInode(tag.getTagId(), inode);
	        	if (tagInode.getTagId() != null) {
	            	DotHibernate.delete(tagInode);
	    		}
	    	}
		}
		return getTagInodeByInode(inode);
	}

	/**
	 * Escape a single quote
	 * @param tagName string with single quotes
	 * @return single quote string escaped
	 */
	private static String escapeSingleQuote(String tagName) {
		return tagName.replace("'", "''");
	}

	/**
	 * Gets a suggested tag(s), by name
	 * @param name name of the tag searched
	 * @return list of suggested tags
	 */
	@SuppressWarnings("unchecked")
	public static List<Tag> getSuggestedTag(String name) {
		try {
			name = escapeSingleQuote(name);

			DotHibernate dh = new DotHibernate(Tag.class);
			dh.setQuery("from tag in class com.dotmarketing.tag.model.Tag where lower(tagname) like ?");
	        dh.setParam(name.toLowerCase() + "%");
            List<Tag> list = dh.list();
        	return list;
		}
		catch (Exception e) {}
		return new ArrayList<Tag>();
	}
	
	/**
	 * Gets all the tags given a user List
	 * @param userIds the user id's associated with the tags
	 * @return a complete list of all the tags, with the owner information and the respective permission
	 * information
	 */
	@SuppressWarnings("unchecked")
	public static List<Tag> getAllTagsForUsers(List<String> userIds) {
		try {	
			DotConnect dc = new DotConnect();
			StringBuilder sb = new StringBuilder();
			sb.append("select tag.tagname, tag.user_id from tag, user_ ");
			sb.append("where tag.user_id = user_.userid ");
			if(userIds!=null && !userIds.isEmpty()){
				sb.append(" and user_.userid in (");
				int count = 0;
				for(String id:userIds){
					if(count>0 && count%500==0){
						count=0;
						sb.append(") or user_.userid in (");
					}
					if(count>0){
						sb.append(", '"+id+"'");
					}else{
						sb.append("'"+id+"'");
					}
					count++;
				}
				sb.append(") ");
			}

			sb.append("order by tag.user_id");
			dc.setSQL(sb.toString());

			List<Tag> tags = new ArrayList<Tag>();
			List<Map<String, Object>> results = (ArrayList<Map<String, Object>>)dc.loadResults();
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> hash = (Map<String, Object>) results.get(i);
				if(!hash.isEmpty()){
					String user_Id = (String) hash.get("user_id");
					String tagName = (String) hash.get("tagname");
					Tag tag = new Tag();
					tag.setTagName(tagName);
					tag.setUserId(user_Id);
					tags.add(tag);
				}
			}	
			return tags;
		}
		catch (Exception e) {
			 Logger.warn(TagFactory.class, "getAllTagsForUsers failed:" + e, e);
		}
		return new ArrayList();
	}
	
}
