package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.NoSuchUserException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilHTML;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

public class CategoriesWebAPI implements ViewTool {

	private CategoryAPI categoryAPI = APILocator.getCategoryAPI();
	private ContentletAPI conAPI = APILocator.getContentletAPI();
	private PermissionAPI perAPI = APILocator.getPermissionAPI();

	private HttpServletRequest request;
	Context ctx;
	User user = null;

	public CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public void setCategoryAPI(CategoryAPI categoryAPI) {
		this.categoryAPI = categoryAPI;
	}

	public void init(Object obj) {
		ViewContext context = (ViewContext) obj;
		this.request = context.getRequest();
		ctx = context.getVelocityContext();
		HttpSession ses = request.getSession(false);
		
		if (ses != null) {
			user = (User) ses.getAttribute(WebKeys.CMS_USER);
			if (user == null && ses.getAttribute("USER_ID") != null) {
				String userId = (String) ses.getAttribute("USER_ID");
				try {
					user = APILocator.getUserAPI().loadUserById(userId, APILocator.getUserAPI().getSystemUser(), false);
				} catch (NoSuchUserException e) {
					Logger.error(this, "A System error happend while trying to retrieve user  : " + userId, e);
				} catch (DotDataException e) {
					Logger.error(this, "A System error happend while trying to retrieve user  : " + userId, e);
				} catch (DotSecurityException e) {
					Logger.error(this, "A System error happend while trying to retrieve user  : " + userId, e);
				}
			}
		}
	}

	@Deprecated
	public String getCategoriesByEntityName(String parameterName, String entityName, boolean displayTitle,
			boolean multipleValues, int size) {
		try {
			long childInode = 0;
			boolean readonly = false;
			return getCategoriesByEntityName(parameterName, entityName, childInode, displayTitle, multipleValues, size,
					readonly);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getCategoriesListByEntityName(String entityName) {
		try {
			Entity e = EntityFactory.getEntity(entityName);
			return EntityFactory.getActiveEntityCategories(e);
		} catch (Exception e) {
			Logger.warn(this, e.toString());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getAllCategoriesListByEntityName(String entityName) {
		try {
			Entity e = EntityFactory.getEntity(entityName);
			return EntityFactory.getEntityCategories(e);
		} catch (Exception e) {
			Logger.warn(this, e.toString());
			return null;
		}
	}

	public List<Category> getChildrenCategoriesByKey(String key) {
		if (key == null) {
			return new ArrayList<Category>();
		}
		try {
			Category cat = categoryAPI.findByKey(key, user, true);
			if (!InodeUtils.isSet(cat.getInode())) {
				return new ArrayList<Category>();
			}
			return categoryAPI.getChildren(cat, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories with key : " + key);
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
		}
		return new ArrayList<Category>();

	}

	public Category getCategoryByKey(String key) {
		if(!UtilMethods.isSet(key)){
			return null;
		}
		try {
			return categoryAPI.findByKey(key, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the category");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve category : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
			return null;
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @deprecated Multiple categories can have the same name so this method
	 *             should be avoid to search a single category
	 */
	public Category getCategoryByName(String name) {
		if(!UtilMethods.isSet(name)){
			return null;
		}
		try {
			return categoryAPI.findByName(name, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the category");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve the category : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve the category : ", e);
			return null;
		}
	}

	public List<Category> getChildrenCategories(Category cat) {
		try {
			return categoryAPI.getChildren(cat, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getChildrenCategories(Inode inode) {
		try {
			List<Category> categories = InodeFactory.getChildrenClass(inode, Category.class);
			return perAPI.filterCollection(categories, PermissionAPI.PERMISSION_READ, true, user);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getChildrenCategories(String inode) {
		try {
			Inode inodeObj = new Inode();
			inodeObj.setInode(inode);
			List<Category> categories = InodeFactory.getChildrenClass(inodeObj, Category.class);
			return perAPI.filterCollection(categories, PermissionAPI.PERMISSION_READ, true, user);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategories(Category cat) {
		try {
			return categoryAPI.getChildren(cat, true, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getActiveChildrenCategoriesByKey(String key) {
		if (key == null) {
			return new ArrayList();
		}
		try {
			Category cat = categoryAPI.findByKey(key, user, true);
			if (!InodeUtils.isSet(cat.getInode())) {
				return new ArrayList();
			}
			return categoryAPI.getChildren(cat, true, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getActiveChildrenCategories(Inode inode) {
		try {
			return categoryAPI.getChildren(inode, true, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategories(String inode) {
		try {
			Inode inodeObj = new Inode();
			inodeObj.setInode(inode);
			return categoryAPI.getChildren(inodeObj, true, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategoriesOrderByName(Category cat) {
		try {
			return categoryAPI.getChildren(cat, true, "category_name", user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategoriesOrderByName(Inode inode) {
		try {
			return categoryAPI.getChildren(inode, true, "category_name", user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategoriesOrderByName(String inode) {
		try {
			Inode inodeObj = new Inode();
			inodeObj.setInode(inode);
			return categoryAPI.getChildren(inodeObj, true, "category_name", user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getActiveChildrenCategoriesByParent(ArrayList<String> o) {
		try {
			List<Category> children = new ArrayList<Category>();
			for (String key : o) {
				if (UtilMethods.isSet(key)) {
					Category cat = getCategoryByKey(key);
					if (!InodeUtils.isSet(cat.getInode())) {
						cat = getCategoryByName(key);
					}
					children.addAll(categoryAPI.getChildren(cat, user, true));
				}
			}
			return children;
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	private List<Map<String, Object>> getAllActiveChildrenCategories(List<Category> children, int currentLevel)
			throws DotDataException, DotSecurityException {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		for (Category ccat : children) {
			Map<String, Object> valMap = new HashMap<String, Object>();
			valMap.put("level", currentLevel);
			valMap.put("category", ccat);
			retList.add(valMap);
			List<Category> cchildren = categoryAPI.getChildren(ccat, true, user, true);
			if (cchildren.size() > 0) {
				List<Map<String, Object>> childrenMaps = getAllActiveChildrenCategories(cchildren, currentLevel + 1);
				retList.addAll(childrenMaps);
			}
		}
		return retList;
	}

	/**
	 * Retrieves a plain list of all the children categories (any depth) of the
	 * given parent category key The list returned is a list of maps, each map
	 * has the category and the level of this category belongs
	 * 
	 * E.G. level: 1 cat: Best Practices level: 1 cat: Conferences &
	 * Presentations level: 2 cat: second level level: 1 cat: Marketing
	 * 
	 * @param key
	 *            parent category key
	 * @return
	 */

	public List<Map<String, Object>> getAllActiveChildrenCategoriesByKey(String key) {
		if (key == null) {
			return new ArrayList<Map<String, Object>>();
		}
		try {
			Category cat = categoryAPI.findByKey(key, user, true);
			if (!InodeUtils.isSet(cat.getInode())) {
				return new ArrayList<Map<String, Object>>();
			}
			List<Category> children = categoryAPI.getChildren(cat, true, user, true);
			return getAllActiveChildrenCategories(children, 1);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/**
	 * Retrieves a plain list of all the children categories (any depth) of the
	 * given parent inode The list returned is a list of maps, each map has the
	 * category and the level of this category belongs
	 * 
	 * E.G. level: 1 cat: Best Practices level: 1 cat: Conferences &
	 * Presentations level: 2 cat: second level level: 1 cat: Marketing
	 * 
	 * @param inode
	 *            parent inode
	 * @return
	 */

	public List<Map<String, Object>> getAllActiveChildrenCategories(Inode inode) {
		try {
			List<Category> children = categoryAPI.getChildren(inode, true, user, true);
			return getAllActiveChildrenCategories(children, 1);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/**
	 * Retrieves a plain list of all the children categories (any depth) of the
	 * given parent inode The list returned is a list of maps, each map has the
	 * category and the level of this category belongs
	 * 
	 * E.G. level: 1 cat: Best Practices level: 1 cat: Conferences &
	 * Presentations level: 2 cat: second level level: 1 cat: Marketing
	 * 
	 * @param inode
	 *            parent inode
	 * @return
	 */

	public List<Map<String, Object>> getAllActiveChildrenCategories(String inode) {
		try {
			Category parent = categoryAPI.find(inode, user, true);
			List<Category> children = categoryAPI.getChildren(parent, true, user, true);
			return getAllActiveChildrenCategories(children, 1);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getInodeCategories(String inode) {
		try {
			Inode inodeObj = new Inode();
			inodeObj.setInode(inode);
			return categoryAPI.getParents(inodeObj, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> getInodeCategories(Inode inodeObj) {
		try {
			return categoryAPI.getParents(inodeObj, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public String getCategoriesByEntityName(String parameterName, String entityName, String childInode,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			String[] selectCatsString = new String[0];
			// Find the selected values
			if (InodeUtils.isSet(childInode)) {
				Inode inode = InodeFactory.getInode(childInode, Inode.class);
				List<Category> categories = categoryAPI.getParents(inode, user, true);
				selectCatsString = new String[categories.size()];
				for (int i = 0; i < categories.size(); i++) {
					selectCatsString[i] = categories.get(i).getInode();
				}
			}

			return getCategoriesByEntityName(parameterName, entityName, selectCatsString, displayTitle, multipleValues,
					size, readonly);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/**
	 * 
	 * @param parameterName
	 *            value of the "name" attribute for the <select> html tag
	 * @param entityName
	 *            name of the entity that have the categories
	 * @param childInode
	 *            Inode value to obtain the selected categories
	 * @param displayTitle
	 *            if do you want that the title display or not (Entity Name)
	 * @param multipleValues
	 *            if the user could select multiple values
	 * @param size
	 *            the height in entries that could be displayed
	 * @param readonly
	 *            if the select is disable
	 * @return
	 */

	@Deprecated
	public String getCategoriesByEntityName(String parameterName, String entityName, long childInode,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			return getCategoriesByEntityName(parameterName, entityName, String.valueOf(childInode), displayTitle,
					multipleValues, size, readonly);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/**
	 * 
	 * @param parameterName
	 *            value of the "name" attribute for the <select> html tag
	 * @param entityName
	 *            name of the entity that have the categories
	 * @param selectedCategories
	 *            Selected categories inodes
	 * @param displayTitle
	 *            if do you want that the title display or not (Entity Name)
	 * @param multipleValues
	 *            if the user could select multiple values
	 * @param size
	 *            the height in entries that could be displayed
	 * @param readonly
	 *            if the select is disable
	 * @return
	 */

	@Deprecated
	public String getCategoriesByEntityName(String parameterName, String entityName, long[] selectedCategories,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		String[] selectCatsString = new String[selectedCategories.length];
		// Find the selected values
		for (int i = 0; i < selectedCategories.length; i++) {
			selectCatsString[i] = Long.toString(selectedCategories[i]);
		}
		try {
			return getCategoriesByEntityName(parameterName, entityName, selectCatsString, displayTitle, multipleValues,
					size, readonly);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/***************************************************************************
	 * This methods return the selects required for the categories of an entity
	 * 
	 * @param parameterName
	 *            value of the "name" attribute for the <select> html tag
	 * @param entityName
	 *            name of the entity that have the categories
	 * @param selectCatsString
	 *            Selected categories inodes
	 * @param displayTitle
	 *            if do you want that the title display or not (Entity Name)
	 * @param multipleValues
	 *            if the user could select multiple values
	 * @param size
	 *            the height in entries that could be displayed
	 * @param readonly
	 *            if the select is disable
	 * @return the the HTML that render the selects
	 */
	@Deprecated
	public String getCategoriesByEntityName(String parameterName, String entityName, String[] selectCatsString,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			StringBuffer buffer = new StringBuffer();
			// GETS THE ENTITY
			Entity entity = EntityFactory.getEntity(entityName);
			// GET ALL THE MAIN CATEGORIES FOR THIS ENTITY
			List<Category> categories = EntityFactory.getEntityCategories(entity);
			String catOptions = "";
			if (categories.size() > 0) {
				Iterator<Category> categoriesIterator = categories.iterator();
				// /FOR EACH CATEGORY WE GET THE CHILDREN
				while (categoriesIterator.hasNext()) {
					StringBuffer buffy = new StringBuffer();
					Category category = (Category) categoriesIterator.next();
					int count = InodeFactory.countChildrenOfClass(category, Category.class);
					if (count > 1) {
						if (displayTitle) {
							buffy.append("<label for='" + parameterName + "'>" + category.getCategoryName()
									+ ":</label>");
						}
						String multiple = (multipleValues ? "multiple='multiple'" : "");
						int sizeAux = (size == -1 ? count - 1 : size);
						String disabled = (readonly ? "disabled" : "");
						buffy.append("<select name='" + parameterName + "'  size='" + sizeAux + "' " + multiple + " "
						// + disabled + " style='width:120px; float:left;'>");
								+ disabled + " >");
						catOptions = UtilHTML.getSelectCategories(category, -1, selectCatsString, user, true);
						buffy.append(catOptions);
						buffy.append("</select>");
						if (catOptions.length() > 1) {
							buffer.append(buffy);
						}
					}
				}
			}
			return buffer.toString();
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@Deprecated
	public String getCategoriesByEntityNameTextMode(String parameterName, String entityName, long[] selectedCats,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			String[] selectCatsString = new String[selectedCats.length];
			// Find the selected values
			for (int i = 0; i < selectedCats.length; i++) {
				selectCatsString[i] = Long.toString(selectedCats[i]);
			}
			return getCategoriesByEntityNameTextMode(parameterName, entityName, selectCatsString, displayTitle,
					multipleValues, size, readonly);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@Deprecated
	public String getCategoriesByEntityNameTextMode(String parameterName, String entityName, long childInode,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		return getCategoriesByEntityNameTextMode(parameterName, entityName, String.valueOf(childInode), displayTitle,
				multipleValues, size, readonly);
	}

	public String getCategoriesByEntityNameTextMode(String parameterName, String entityName, String childInode,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			String[] selectCatsString = new String[0];
			// Find the selected values
			if (InodeUtils.isSet(childInode)) {
				Inode inode = InodeFactory.getInode(childInode, Inode.class);
				List<Category> categories = categoryAPI.getParents(inode, user, true);
				selectCatsString = new String[categories.size()];
				for (int i = 0; i < categories.size(); i++) {
					selectCatsString[i] = categories.get(i).getInode();
				}
			}

			return getCategoriesByEntityNameTextMode(parameterName, entityName, selectCatsString, displayTitle,
					multipleValues, size, readonly);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@Deprecated
	public String getCategoriesByEntityNameTextMode(String parameterName, String entityName, String[] selectCatsString,
			boolean displayTitle, boolean multipleValues, int size, boolean readonly) {
		try {
			StringBuffer buffer = new StringBuffer();
			// GETS THE ENTITY
			Entity entity = EntityFactory.getEntity(entityName);
			// GET ALL THE MAIN CATEGORIES FOR THIS ENTITY
			List<Category> categories = EntityFactory.getEntityCategories(entity);
			if (categories.size() > 0) {
				buffer.append("<table cellpadding=\"0\" cellspacing=\"0\">");
				buffer.append("<tr>");
				buffer.append("<td align='left' colspan='2'>");
				buffer.append("<table cellpadding=\"0\" cellspacing=\"0\">");
				Iterator<Category> categoriesIterator = categories.iterator();
				// /FOR EACH CATEGORY WE GET THE CHILDREN
				while (categoriesIterator.hasNext()) {
					Category category = (Category) categoriesIterator.next();
					int count = InodeFactory.countChildrenOfClass(category, Category.class);
					if (count > 1) {
						buffer.append("<tr>");
						if (displayTitle) {
							buffer.append("<td valign='top' nowrap>");
							buffer.append("<B>" + category.getCategoryName() + ":</B>&nbsp;&nbsp;");
							buffer.append("</td>");
						}
						buffer.append("<td align='left'>");
						// String multiple = (multipleValues ?
						// "multiple='multiple'"
						// : "");
						// int sizeAux = (size == -1 ? count -1 : size);
						// String disabled = (readonly ? "disabled" : "");
						// String mode = "";
						buffer.append("<table cellpadding=\"0\" cellspacing=\"0\">");
						buffer.append(UtilHTML.getSelectCategoriesTextMode(category, 1, selectCatsString, user, true));
						buffer.append("</table>");
						buffer.append("</td>");
						buffer.append("</tr>");
					}
				}
				buffer.append("</table>");
				buffer.append("</td>");
				buffer.append("</tr>");
				buffer.append("</table>");
			}
			return buffer.toString();
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	/**
	 * 
	 * @param parameterName
	 *            value of the "name" attribute for the <select> html tag
	 * @param entityName
	 *            name of the entity that have the categories
	 * @param selectedCategories
	 *            Selected categories inodes
	 * @param displayTitle
	 *            if do you want that the title display or not (Entity Name)
	 * @param multipleValues
	 *            if the user could select multiple values
	 * @param size
	 *            the height in entries that could be displayed
	 * @param readonly
	 *            if the select is disable
	 * @return
	 */
	@Deprecated
	public String getSimpleComboByEntityName(String parameterName, String entityName, long selectedCategory) {
		try {
			String[] selectCatsString = new String[1];
			selectCatsString[0] = Long.toString(selectedCategory);
			return getCategoriesByEntityName(parameterName, entityName, selectCatsString, false, false, 1, false);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	@Deprecated
	public String getSimpleCategoriesByEntityName(String parameterName, String entityName, long selectedCategory) {
		try {
			String[] selectCatsString = new String[1];
			selectCatsString[0] = Long.toString(selectedCategory);
			return getCategoriesByEntityNameTextMode(parameterName, entityName, selectCatsString, false, false, 1,
					false);
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public Category getCategoryByInode(String inode) {
		try {
			return (Category) categoryAPI.find(inode, user, true);
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the category");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve category : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
			return null;
		}
	}

	@Deprecated
	public Category getCategoryByInode(long inode) {
		try {
			return getCategoryByInode(String.valueOf(inode));
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
			return null;
		}
	}

	@Deprecated
	public String getCategoryKeyByContentlet(long contentletInode) {
		try {
			return getCategoryKeyByContentlet(String.valueOf(contentletInode));
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
			return null;
		}
	}

	public String getCategoryKeyByContentlet(String contentletInode) {
		try {
			Contentlet contentlet = new Contentlet();
			try {
				contentlet = conAPI.find(contentletInode, user, true);
			} catch (DotDataException e) {
				Logger.error(this, "Unable to look up contentlet with inode " + contentletInode, e);
			}
			List<Category> category = categoryAPI.getParents(contentlet, user, true);
			// Category category = (Category)
			// InodeFactory.getParentOfClass(contentlet,Category.class);
			String key = category.get(0).getKey();
			return key;
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the category");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve category : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Category> getCategoriesByUser(User user) {
		try {
			HttpSession session = request.getSession();
			List<Category> catsUser = (List<Category>) session.getAttribute(WebKeys.LOGGED_IN_USER_CATS);
			if (!UtilMethods.isSet(catsUser) || catsUser.size() == 0) {
				UserProxy up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,
						APILocator.getUserAPI().getSystemUser(), false);
				catsUser = categoryAPI.getChildren(up, user, true);
				request.getSession().setAttribute(WebKeys.LOGGED_IN_USER_CATS, catsUser);
			}
			return catsUser;
		} catch (DotSecurityException se) {
			Logger.info(this, "The logged in user cannot access the categories");
			return null;
		} catch (DotDataException de) {
			Logger.error(this, "An error happening while trying to retrieve categories : ", de);
			return null;
		} catch (Exception e) {
			Logger.error(this, "An unknown error happening while trying to retrieve categories : ", e);
			return null;
		}
	}

	public List<Category> filterCategoriesByUserPermissions(List<Object> catInodes) {
		List<Category> result = new ArrayList<Category>(30);
		try {
			// Needed to make the List Generic when we refactored to UUID to
			// handle backwards compat of categores being passed.
			for (Object cInode : catInodes) {
				String catInode = cInode.toString();
				try {
					result.add(categoryAPI.find(catInode, user, true));
				} catch (DotSecurityException se) {
				} catch (DotDataException de) {
					Logger.error(this, "An error happening while trying to retrieve category : ", de);
				} catch (Exception e) {
					Logger.error(this, "An unknown error happening while trying to retrieve category : ", e);
				}
			}
		} catch (Exception e) {
			Logger.warn(this, e.toString());
		}

		return result;
	}
}